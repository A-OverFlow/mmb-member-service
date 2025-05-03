package com.mumulbo.member.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.repository.MemberRepository
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class MemberControllerTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var member: Member

    @BeforeEach
    fun init() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        member = memberRepository.save(Member(provider, providerId, name, email, profile))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("성공-createOrGetMember")
    @Test
    fun `success-createOrGetMember`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        val request = MemberCreateOrGetRequest(provider, providerId, name, email, profile)

        // when // then
        mockMvc.perform(
            post("/api/v1/members", request)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").isNumber)
    }

    @DisplayName("성공-getMyInfo")
    @Test
    fun `success-getMyInfo`() {
        // when // then
        mockMvc.perform(
            get("/api/v1/members/me")
                .header("X-User-Id", member.id)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name", `is`(member.name)))
            .andExpect(jsonPath("$.email", `is`(member.email)))
            .andExpect(jsonPath("$.profile", `is`(member.profile)))
    }

    @DisplayName("성공-updateMember")
    @Test
    fun `success-updateMember`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        val member = Member(provider, providerId, name, email, profile)
        memberRepository.save(member)

        val request = MemberUpdateRequest("송준희2")

        // when // then
        mockMvc.perform(
            put("/api/v1/members/{id}", member.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name", `is`(request.name)))
    }

    @DisplayName("성공-deleteMember")
    @Test
    fun `success-deleteMember`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        val member = memberRepository.save(Member(provider, providerId, name, email, profile))

        // when // then
        mockMvc.perform(
            delete("/api/v1/members/{id}", member.id)
        )
            .andExpect(status().isNoContent)
    }
}
