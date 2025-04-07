package com.mumulbo.auth.service

import com.mumulbo.member.enums.Role
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.Date
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}") private val secretKey: String,
    @Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpirationMillis: Long,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpirationMillis: Long
) {
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun generateRefreshToken(): String {
        val issuedAt = Date()
        val expiration = Date(issuedAt.time + refreshTokenExpirationMillis)

        return Jwts.builder()
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateAccessToken(id: Long, username: String, role: Role): String {
        val issuedAt = Date()
        val expiration = Date(issuedAt.time + accessTokenExpirationMillis)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .claim("id", id)
            .claim("role", role)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}
