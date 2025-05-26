package com.mumulbo.profile.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.dto.MultipartFileWrapper
import com.mumulbo.profile.entity.Profile
import com.mumulbo.profile.exception.InvalidFileException
import java.net.URI
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers
import ulid.ULID

@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension::class)
@Transactional
class ProfileServiceTest : TestContainers() {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var profileService: ProfileService

    @MockitoBean
    private lateinit var fileService: FileService

    @Value("\${minio.bucket}")
    private lateinit var bucket: String

    @DisplayName("성공-saveProfile")
    @Test
    fun `success-saveProfile`() {
        // given
        // mock
        val picture = "https://lh3.googleusercontent.com/a/abcdefg"
        val objectName = ULID.nextULID().toString()
        val file = MultipartFileWrapper(
            byteArrayOf(
                0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
                0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
            ),
            "profile.png",
            "image/png",
        )
        `when`(fileService.urlToMultipartFile(URI.create(picture).toURL())).thenReturn(file)
        `when`(fileService.uploadImage(file)).thenReturn(objectName)

        // when
        val profile = profileService.saveProfile(picture)

        // then
        assertThat(profile.picture).isEqualTo(objectName)
        verify(fileService).urlToMultipartFile(URI.create(picture).toURL())
        verify(fileService).uploadImage(file)
    }

    @DisplayName("실패-saveProfile")
    @Test
    fun `fail-saveProfile`() {
        // given
        // mock
        val picture = "https://lh3.googleusercontent.com/a/abcdefg"
        val file = MultipartFileWrapper(
            "text file".toByteArray(),
            "text.txt",
            "text/plain",
        )
        `when`(fileService.urlToMultipartFile(URI.create(picture).toURL())).thenReturn(file)
        `when`(fileService.uploadImage(file)).thenThrow(InvalidFileException::class.java)

        // when // then
        assertThatThrownBy { profileService.saveProfile(picture) }
            .isInstanceOf(InvalidFileException::class.java)
        verify(fileService).urlToMultipartFile(URI.create(picture).toURL())
        verify(fileService).uploadImage(file)
    }

    @DisplayName("성공-getProfile")
    @Test
    fun `success-getProfile`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val picture = "abcdefgh"

        val profile = Profile(picture)
        val member = memberRepository.save(Member(provider, providerId, name, email, profile))

        val id = member.id!!

        // when
        val response = profileService.getProfile(id)

        // then
        assertThat(response).isNotNull
            .extracting("name", "email", "picture")
            .contains(name, email, "$bucket/profiles/${member.profile.picture}")
    }

    @DisplayName("fail-getProfile")
    @Test
    fun `fail-getProfile`() {
        // given
        val id = 999_999L

        // when // then
        assertThatThrownBy { profileService.getProfile(id) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }

    @DisplayName("성공-updatePicture")
    @Test
    fun `success-updatePicture`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val picture = "abcdefgh"

        val profile = Profile(picture)
        val member = memberRepository.save(Member(provider, providerId, name, email, profile))

        val id = member.id!!
        val objectName = ULID.nextULID().toString()
        val file = MultipartFileWrapper(
            byteArrayOf(
                0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
                0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
            ),
            "profile.png",
            "image/png",
        )
        `when`(fileService.uploadImage(file)).thenReturn(objectName)

        // when
        val response = profileService.updatePicture(id, file)

        // then
        assertThat(response.picture).isEqualTo("$bucket/profiles/$objectName")
        verify(fileService).uploadImage(file)
    }

    @DisplayName("실패-updatePicture(user not exists)")
    @Test
    fun `fail-updatePicture(user not exists)`() {
        // given
        val id = 999_999L
        val file = MultipartFileWrapper(
            byteArrayOf(
                0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
                0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
            ),
            "profile.png",
            "image/png",
        )

        // when // then
        assertThatThrownBy { profileService.updatePicture(id, file) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }

    @DisplayName("실패-updatePicture(invalid file)")
    @Test
    fun `fail-updatePicture(invalid file)`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val picture = "abcdefgh"

        val profile = Profile(picture)
        val member = memberRepository.save(Member(provider, providerId, name, email, profile))

        val id = member.id!!
        val file = MultipartFileWrapper(
            "text file".toByteArray(),
            "text.txt",
            "text/plain",
        )
        `when`(fileService.uploadImage(file)).thenThrow(InvalidFileException::class.java)

        // when // then
        assertThatThrownBy { profileService.updatePicture(id, file) }
            .isInstanceOf(InvalidFileException::class.java)
    }
}
