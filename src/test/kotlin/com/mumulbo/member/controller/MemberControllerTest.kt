package com.mumulbo.member.controller

import com.mumulbo.auth.service.JwtTokenProvider
import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
import com.mumulbo.member.repository.MemberRepository
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var member: Member

    @BeforeEach
    fun init() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        val password = "password"
        member = memberRepository.save(Member(name, email, username, password))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("성공-회원 정보 조회")
    @Test
    fun `success-get member`() {
        // given
        val accessToken = jwtTokenProvider.generateAccessToken(member.id!!, member.username, member.role)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/members/me")
                .header("Authorization", "Bearer $accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.name", `is`(member.name)))
            .andExpect(jsonPath("$.email", `is`(member.email)))
            .andExpect(jsonPath("$.username", `is`(member.username)))
    }

    @DisplayName("실패-토큰이 header에 없는 경우")
    @Test
    fun `fail-token is not found in header`() {
        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/members/me")
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.UNAUTHORIZED.value())))
            .andExpect(jsonPath("$.error", `is`("AUTH-001")))
            .andExpect(jsonPath("$.message", `is`("인증되지 않은 요청입니다.")))
    }

    @DisplayName("실패-존재하지 않는 회원을 조회할 경우")
    @Test
    fun `fail-member not found`() {
        // given
        val accessToken = jwtTokenProvider.generateAccessToken(999_999L, member.username, member.role)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/members/me")
                .header("Authorization", "Bearer $accessToken")
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.value())))
            .andExpect(jsonPath("$.error", `is`("MEMBER-001")))
            .andExpect(jsonPath("$.message", `is`("존재하지 않는 회원입니다.")))
    }
}
