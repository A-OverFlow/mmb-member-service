package com.mumulbo.auth.repository

import java.time.Duration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisRepository(
    private val redisTemplate: StringRedisTemplate
) {
    private val REFRESH_PREFIX = "refresh:member:"

    fun save(id: Long, refreshToken: String, duration: Duration = Duration.ofDays(7)) {
        val key = REFRESH_PREFIX + id
        redisTemplate.opsForValue().set(key, refreshToken, duration)
    }

    fun get(id: Long): String? {
        val key = REFRESH_PREFIX + id
        return redisTemplate.opsForValue().get(key)
    }
}
