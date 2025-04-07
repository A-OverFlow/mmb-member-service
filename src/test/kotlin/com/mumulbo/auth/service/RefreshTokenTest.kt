package com.mumulbo.auth.service

import com.mumulbo.auth.exception.InvalidTokenException
import com.mumulbo.auth.repository.RedisRepository
import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
import com.mumulbo.member.repository.MemberRepository
import java.time.Duration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
class RefreshTokenTest : TestContainers() {
    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var redisRepository: RedisRepository

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

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

    @DisplayName("성공-토큰 재발행")
    @Test
    fun `success-refresh token`() {
        // given
        val username = "joonhee.song"
        val refreshToken = jwtTokenProvider.generateRefreshToken(username)
        redisRepository.save(1L, refreshToken)

        // when
        val response = authService.refreshToken(refreshToken)

        // then
        assertThat(jwtTokenProvider.getUsernameFromToken(response.refreshToken)).isEqualTo(username)
    }

    @DisplayName("실패-유효하지 않은 토큰")
    @Test
    fun `fail-invalid token`() {
        // given
        val refreshToken = "invalid token"

        // when // then
        assertThatThrownBy { authService.refreshToken(refreshToken) }
            .isInstanceOf(InvalidTokenException::class.java)
    }

    @DisplayName("실패-만료된 토큰")
    @Test
    fun `fail-token expired`() {
        // given
        val username = "joonhee.song"
        val refreshToken = jwtTokenProvider.generateRefreshToken(username)
        redisRepository.save(1L, refreshToken, Duration.ofMillis(1L))

        // when // then
        assertThatThrownBy { authService.refreshToken(refreshToken) }
            .isInstanceOf(InvalidTokenException::class.java)
    }

    @DisplayName("실패-변조된 토큰")
    @Test
    fun `fail-token tampered`() {
        // given
        val username = "joonhee.song"
        val refreshToken = jwtTokenProvider.generateRefreshToken(username)
        redisRepository.save(1L, refreshToken)

        val temperedRefreshToken = jwtTokenProvider.generateRefreshToken(username)

        // when // then
        assertThatThrownBy { authService.refreshToken(temperedRefreshToken) }
            .isInstanceOf(InvalidTokenException::class.java)
    }
}
