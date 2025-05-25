package com.mumulbo.member.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.dto.MultipartFileWrapper
import com.mumulbo.profile.entity.Profile
import com.mumulbo.profile.service.FileService
import java.net.URI
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
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
class MemberServiceTest : TestContainers() {
    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @MockitoBean
    private lateinit var fileService: FileService

    @Value("\${minio.bucket}")
    private lateinit var bucket: String

    private lateinit var member: Member

    @BeforeEach
    fun init() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val picture = "abcdefgh"

        val profile = Profile(picture)
        member = memberRepository.save(Member(provider, providerId, name, email, profile))
    }

    @DisplayName("성공-createOrGetMember(create)")
    @Test
    fun `success-createMember(create)`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "abcdefghijklmnopqr"
        val name = "송준희2"
        val email = "mike.urssu2@gmail.com"
        val picture = "https://lh3.googleusercontent.com/a/hijklmn"
        val request = MemberCreateOrGetRequest(provider, providerId, name, email, picture)

        val objectName = ULID.nextULID().toString()
        val file = MultipartFileWrapper(
            byteArrayOf(
                0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
                0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
            ),
            objectName,
            "image.png",
        )
        Mockito.`when`(fileService.urlToMultipartFile(URI.create(picture).toURL())).thenReturn(file)
        Mockito.`when`(fileService.uploadImage(file)).thenReturn(objectName)

        // when
        val response = memberService.createOrGetMember(request)

        // then
        assertThat(response.id).isPositive()
        Mockito.verify(fileService).urlToMultipartFile(URI.create(picture).toURL())
        Mockito.verify(fileService).uploadImage(file)
    }

    @DisplayName("성공-createOrGetMember(get)")
    @Test
    fun `success-createMember(get)`() {
        // given
        val provider = member.provider
        val providerId = member.providerId
        val name = member.name
        val email = member.email
        val picture = "https://lh3.googleusercontent.com/a/hijklmn"
        val request = MemberCreateOrGetRequest(provider, providerId, name, email, picture)

        // when
        val response = memberService.createOrGetMember(request)

        // then
        assertThat(response.id).isEqualTo(member.id)
    }

    @DisplayName("성공-getMember")
    @Test
    fun `success-getMember`() {
        // given
        val id = member.id!!

        // when
        val response = memberService.getMember(id)

        // then
        assertThat(response)
            .extracting("name", "email", "picture")
            .contains(member.name, member.email, "$bucket/profiles/${member.profile.picture}")
    }

    @DisplayName("실패-getMember")
    @Test
    fun `fail-getMember`() {
        // given
        val id = 999_999L

        // when // then
        assertThatThrownBy { memberService.getMember(id) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }

    @DisplayName("성공-deleteMember")
    @Test
    fun `success-deleteMember`() {
        // given
        val id = member.id!!

        // when
        memberService.deleteMember(id)

        // then
        assertThat(memberRepository.existsById(member.id!!)).isFalse()
    }

    @DisplayName("실패-deleteMember")
    @Test
    fun `fail-deleteMember`() {
        // given
        val id = 999_999L

        // when // then
        assertThatThrownBy { memberService.deleteMember(id) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }
}
