package com.mumulbo.member.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberCreateRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.repository.MemberRepository
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
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

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("성공-createMember")
    @Test
    fun `success-createMember`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        val request = MemberCreateRequest(provider, providerId, name, email, profile)

        // when // then
        mockMvc.perform(
            post("/api/v1/members", request)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name", `is`(request.name)))
            .andExpect(jsonPath("$.email", `is`(request.email)))
    }

    @DisplayName("성공-checkMember")
    @Test
    fun `success-checkMember`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        val member = Member(provider, providerId, name, email, profile)
        memberRepository.save(member)

        // when // then
        mockMvc.perform(
            get("/api/v1/members/check")
                .param("email", email)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id", `is`(member.id!!.toInt())))
    }

    @DisplayName("성공-getMember")
    @Test
    fun `success-getMember`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        val member = Member(provider, providerId, name, email, profile)
        memberRepository.save(member)

        // when // then
        mockMvc.perform(
            get("/api/v1/members/{id}", member.id)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name", `is`(member.name)))
            .andExpect(jsonPath("$.email", `is`(member.email)))
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
