package com.mumulbo.auth.RedisRepository

import com.mumulbo.auth.repository.RedisRepository
import java.time.Duration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class GetTest {
    @Autowired
    private lateinit var redisRepository: RedisRepository

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    private val id = 1L
    private val refreshToken = "refresh token"

    companion object {
        @Container
        private val redisContainer: GenericContainer<*> = GenericContainer("redis:7.0.5-alpine")
            .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        private fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379) }
        }
    }

    @AfterEach
    fun cleansing() {
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }

    @Test
    @DisplayName("성공-refresh token 조회")
    fun `success-get refresh token`() {
        // given
        val key = "refresh:member:$id"
        val duration = Duration.ofDays(7)
        redisTemplate.opsForValue().set(key, refreshToken, duration)

        // when // then
        val token = redisRepository.get(id)
        assertThat(token).isEqualTo(refreshToken)
    }
}
