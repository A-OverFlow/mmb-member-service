package com.mumulbo.member.application.controller.memberController

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.member.application.request.MemberSignInRequest
import com.mumulbo.member.config.TestContainers
import com.mumulbo.member.domain.model.entity.Member
import com.mumulbo.member.domain.model.repository.MemberRepository
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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
class MemberControllerTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun init() {
        // given
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        memberRepository.save(Member(name, email))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("로그인 성공")
    @Test
    fun `sign in succeeded`() {
        // given
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val request = MemberSignInRequest(name, email)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/members/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(Matchers.isA(Long::class.java), Long::class.java))
            .andExpect(jsonPath("$.name", `is`(name)))
            .andExpect(jsonPath("$.email", `is`(email)))
    }

    @DisplayName("로그인 실패 - 비정상적인 입력")
    @Test
    fun `sign in failed - invalid request`() {
        // given
        val name = "name is too long"
        val email = "invalid email format"
        val request = MemberSignInRequest(name, email)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/members/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.BAD_REQUEST.value())))
            .andExpect(jsonPath("$.error", `is`("System-001")))
    }

    @DisplayName("로그인 실패 - 존해자히 않는 사용자")
    @Test
    fun `sign in failed - member not found`() {
        // given
        val name = "anonymous"
        val email = "anonymous@ahnlab.com"
        val request = MemberSignInRequest(name, email)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/members/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.value())))
            .andExpect(jsonPath("$.error", `is`("MEMBER-001")))
            .andExpect(jsonPath("$.message", `is`("존재하지 않는 사용자입니다.")))
    }
}
