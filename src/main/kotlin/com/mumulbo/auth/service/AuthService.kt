package com.mumulbo.auth.service

import com.mumulbo.auth.repository.RefreshTokenRepository
import com.mumulbo.member.dto.request.MemberSignInRequest
import com.mumulbo.member.dto.request.MemberSignUpRequest
import com.mumulbo.member.dto.response.MemberSignInResponse
import com.mumulbo.member.dto.response.MemberSignUpResponse
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberAlreadyExistsException
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import java.time.Duration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder
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

    fun signIn(request: MemberSignInRequest): MemberSignInResponse {
        val member = memberRepository.findByUsername(request.username) ?: throw MemberNotFoundException()
        if (!passwordEncoder.matches(request.password, member.password)) {
            throw MemberNotFoundException()
        }

        val refreshToken = jwtTokenProvider.generateRefreshToken()
        val accessToken = jwtTokenProvider.generateAccessToken(member.id!!, member.username, member.role)

        refreshTokenRepository.save(member.id!!, refreshToken, Duration.ofDays(7))

        return MemberSignInResponse(refreshToken, accessToken)
    }
}
