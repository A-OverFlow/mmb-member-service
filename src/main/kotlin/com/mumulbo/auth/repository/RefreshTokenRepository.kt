package com.mumulbo.auth.repository

import java.time.Duration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRepository(
    private val redisTemplate: StringRedisTemplate
) {
    private val REFRESH_PREFIX = "refresh:member:"

    fun save(id: Long, refreshToken: String, duration: Duration) {
        val key = REFRESH_PREFIX + id
        redisTemplate.opsForValue().set(key, refreshToken, duration)
    }
}
