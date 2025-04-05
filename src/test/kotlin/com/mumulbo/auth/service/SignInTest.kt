package com.mumulbo.auth.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberSignInRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@ExtendWith(MockitoExtension::class)
class SignInTest : TestContainers() {
    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

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
        val password = passwordEncoder.encode("password")
        memberRepository.save(Member(name, email, username, password))
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

        // when
        val response = authService.signIn(request)

        // then
        assertThat(response.refreshToken).isNotNull()
        assertThat(response.accessToken).isNotNull()

        val member = memberRepository.findByUsername(username)!!
        val refreshToken = redisTemplate.opsForValue().get("refresh:member:${member.id}")
        assertThat(response.refreshToken).isEqualTo(refreshToken)
    }

    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    @Test
    fun `fail-member not found`() {
        // given
        val username = "anonymous"
        val password = "anonymous"
        val request = MemberSignInRequest(username, password)

        // when //then
        assertThatThrownBy { authService.signIn(request) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }
}
