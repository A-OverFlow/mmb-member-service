package com.mumulbo.auth.service

import com.mumulbo.member.dto.request.MemberSignUpRequest
import com.mumulbo.member.dto.response.MemberSignUpResponse
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberAlreadyExistsException
import com.mumulbo.member.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val memberRepository: MemberRepository,
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
}
