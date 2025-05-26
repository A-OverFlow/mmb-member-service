package com.mumulbo.profile.controller

import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.entity.Profile
import com.mumulbo.profile.service.FileService
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
import ulid.ULID

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ExtendWith(MockitoExtension::class)
class ProfileControllerTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

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

    @DisplayName("성공-getProfile")
    @Test
    fun `success-createOrGetMember`() {
        // when // then
        mockMvc.perform(
            get("/api/v1/members/me/profile")
                .header("X-User-Id", member.id)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name", `is`(member.name)))
            .andExpect(jsonPath("$.email", `is`(member.email)))
            .andExpect(jsonPath("$.picture", `is`("$bucket/profiles/${member.profile.picture}")))
            .andExpect(jsonPath("$.introduction", `is`(member.profile.introduction)))
            .andExpect(jsonPath("$.website", `is`(member.profile.website)))
    }

    @DisplayName("성공-updatePicture")
    @Test
    fun `success-updatePicture`() {
        // given
        val id = member.id!!

        val objectName = ULID.nextULID().toString()
        val file = MockMultipartFile(
            "file",
            "profile.png",
            "image/png",
            byteArrayOf(
                0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
                0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
            ),
        )
        `when`(fileService.uploadImage(file)).thenReturn(objectName)

        // when // then
        mockMvc.perform(
            multipart("/api/v1/members/me/profile/picture")
                .file(file)
                .header("X-User-Id", id)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with { it.method = "PUT"; it }
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.picture", `is`("$bucket/profiles/$objectName")))
    }
}
