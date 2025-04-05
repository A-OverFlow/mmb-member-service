package com.mumulbo.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberSignUpRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.repository.MemberRepository
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class SignUpTest : TestContainers() {
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

    @DisplayName("회원가입 성공")
    @Test
    fun `sign up succeeded`() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        val password = "password"
        val request = MemberSignUpRequest(name, email, username, password)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name", `is`(name)))
            .andExpect(jsonPath("$.email", `is`(email)))
            .andExpect(jsonPath("$.username", `is`(username)))
    }

    @DisplayName("회원가입 실패 - 유효하지 않은 입력값")
    @Test
    fun `sign up failed - invalid request`() {
        // given
        val name = "송준희"
        val email = "invalid email"
        val username = "joonhee.song"
        val password = "password"
        val request = MemberSignUpRequest(name, email, username, password)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.BAD_REQUEST.value())))
            .andExpect(jsonPath("$.error", `is`("System-001")))
    }

    @DisplayName("회원가입 실패 - 이미 가입한 사용자")
    @Test
    fun `sign up failed - member already exists`() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        val password = "password"
        memberRepository.save(Member(name, email, username, password))

        val request = MemberSignUpRequest(name, email, username, password)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.CONFLICT.value())))
            .andExpect(jsonPath("$.error", `is`("MEMBER-002")))
            .andExpect(jsonPath("$.message", `is`("이미 가입한 회원입니다.")))
    }
}
