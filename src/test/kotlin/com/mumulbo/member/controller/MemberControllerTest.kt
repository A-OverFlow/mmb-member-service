package com.mumulbo.member.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.dto.MultipartFileWrapper
import com.mumulbo.profile.entity.Profile
import com.mumulbo.profile.service.FileService
import java.net.URI
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers
import ulid.ULID

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ExtendWith(MockitoExtension::class)
@Transactional
class MemberControllerTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var fileService: FileService

    @Value("\${minio.bucket}")
    private lateinit var bucket: String

    private lateinit var member: Member

    @BeforeEach
    fun init() {
        // given
        val provider = Provider.GOOGLE
        val providerId = ULID.nextULID().toString()
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val nickname = "송준희"
        val picture = "abcdefgh"

        val profile = Profile(nickname, picture)
        member = memberRepository.save(Member(provider, providerId, name, email, profile))
    }

    @DisplayName("성공-createOrGetMember(create)")
    @Test
    fun `success-createOrGetMember(create)`() {
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
            "image/png",
        )
        Mockito.`when`(fileService.urlToMultipartFile(URI.create(picture).toURL())).thenReturn(file)
        Mockito.`when`(fileService.uploadImage(file)).thenReturn(objectName)

        // when // then
        mockMvc.perform(
            post("/api/v1/members", request)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").isNumber)
    }

    @DisplayName("성공-createOrGetMember(get)")
    @Test
    fun `success-createOrGetMember(get)`() {
        // given
        val provider = member.provider
        val providerId = member.providerId
        val name = member.name
        val email = member.email
        val picture = "https://lh3.googleusercontent.com/a/hijklmn"
        val request = MemberCreateOrGetRequest(provider, providerId, name, email, picture)

        // when // then
        mockMvc.perform(
            post("/api/v1/members", request)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(member.id!!.toInt())))
    }

    @DisplayName("성공-getMyInfo")
    @Test
    fun `success-getMyInfo`() {
        // given
        val id = member.id!!

        // when // then
        mockMvc.perform(
            get("/api/v1/members/me")
                .header("X-User-Id", id)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(member.id!!.toInt())))
            .andExpect(jsonPath("$.name", `is`(member.name)))
            .andExpect(jsonPath("$.email", `is`(member.email)))
            .andExpect(jsonPath("$.nickname", `is`(member.profile.nickname)))
            .andExpect(jsonPath("$.picture", `is`("$bucket/profiles/${member.profile.picture}")))
    }

    @DisplayName("성공-deleteMyInfo")
    @Test
    fun `success-deleteMyInfo`() {
        // given
        val id = member.id!!

        // when // then
        mockMvc.perform(
            delete("/api/v1/members/me")
                .header("X-User-Id", id)
        )
            .andExpect(status().isNoContent)
    }

    @DisplayName("성공-countMembers")
    @Test
    fun `success-countMembers`() {
        // given
        val count = memberRepository.count().toInt()

        // when // then
        mockMvc.perform(
            get("/api/v1/members/total")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.count", `is`(count)))
    }
}
