package com.mumulbo.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberSignInRequest
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
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class SignInTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        @Container
        private val redisContainer: GenericContainer<*> = GenericContainer("redis:7.0.5-alpine")
            .withExposedPorts(6379)

        @DynamicPropertySource
        @JvmStatic
        private fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379) }
        }
    }

    @BeforeEach
    fun init() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        val password = "password"
        memberRepository.save(Member(name, email, username, passwordEncoder.encode(password)))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }

    @DisplayName("로그인 성공")
    @Test
    fun `success-sign in`() {
        // given
        val username = "joonhee.song"
        val password = "password"
        val request = MemberSignInRequest(username, password)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.refreshToken").isString)
            .andExpect(jsonPath("$.accessToken").isString)
    }

    @DisplayName("로그인 실패 - 비정상적인 입력")
    @Test
    fun `fail-invalid request`() {
        // given
        val username = "abcdefghijklmnopqrstuvwxyz"
        val password = "password"
        val request = MemberSignInRequest(username, password)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.BAD_REQUEST.value())))
            .andExpect(jsonPath("$.error", `is`("System-001")))
    }

    @DisplayName("로그인 실패 - 존해자히 않는 사용자")
    @Test
    fun `fail-member not found`() {
        // given
        val username = "anonymous"
        val password = "anonymous"
        val request = MemberSignInRequest(username, password)

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.value())))
            .andExpect(jsonPath("$.error", `is`("MEMBER-001")))
            .andExpect(jsonPath("$.message", `is`("존재하지 않는 회원입니다.")))
    }
}
