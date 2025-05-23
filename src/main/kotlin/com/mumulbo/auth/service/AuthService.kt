package com.mumulbo.auth.service

import com.mumulbo.auth.dto.response.TokenResponse
import com.mumulbo.auth.exception.InvalidTokenException
import com.mumulbo.auth.repository.RedisRepository
import com.mumulbo.member.dto.request.MemberSignInRequest
import com.mumulbo.member.dto.request.MemberSignUpRequest
import com.mumulbo.member.dto.response.MemberSignUpResponse
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberAlreadyExistsException
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val redisRepository: RedisRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) {
    fun signUp(request: MemberSignUpRequest): MemberSignUpResponse {
        if (memberRepository.existsByUsername(request.username)) {
            throw MemberAlreadyExistsException()
        }

        val member = Member.of(
            request.name,
            request.email,
            request.username,
            passwordEncoder.encode(request.password)
        )
        memberRepository.save(member)
        return MemberSignUpResponse.of(member)
    }

    fun signIn(request: MemberSignInRequest): TokenResponse {
        val member = memberRepository.findByUsername(request.username) ?: throw MemberNotFoundException()
        if (!passwordEncoder.matches(request.password, member.password)) {
            throw MemberNotFoundException()
        }

        val refreshToken = jwtTokenProvider.generateRefreshToken(member.username)
        val accessToken = jwtTokenProvider.generateAccessToken(member.id!!, member.username, member.role)

        redisRepository.save(member.id!!, refreshToken)

        return TokenResponse(refreshToken, accessToken)
    }

    fun refreshToken(refreshToken: String): TokenResponse {
        if (!jwtTokenProvider.isValid(refreshToken)) {
            throw InvalidTokenException()
        }

        val username = jwtTokenProvider.getUsernameFromToken(refreshToken)
        val member = memberRepository.findByUsername(username) ?: throw MemberNotFoundException()

        val token = redisRepository.get(member.id!!)
        if (refreshToken != token) {
            throw InvalidTokenException()
        }

        val newRefreshToken = jwtTokenProvider.generateRefreshToken(member.username)
        val newAccessToken = jwtTokenProvider.generateAccessToken(member.id!!, member.username, member.role)
        redisRepository.save(member.id!!, newRefreshToken)

        return TokenResponse(newRefreshToken, newAccessToken)
    }
}
