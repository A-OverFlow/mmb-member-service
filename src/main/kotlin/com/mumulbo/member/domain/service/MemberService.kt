package com.mumulbo.member.domain.service

import com.mumulbo.member.application.request.MemberSignInRequest
import com.mumulbo.member.application.request.MemberSignUpRequest
import com.mumulbo.member.application.response.MemberSignInResponse
import com.mumulbo.member.application.response.MemberSignUpResponse
import com.mumulbo.member.domain.model.entity.Member
import com.mumulbo.member.domain.model.exception.MemberAlreadyExistsException
import com.mumulbo.member.domain.model.exception.MemberNotFoundException
import com.mumulbo.member.domain.model.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun signUp(request: MemberSignUpRequest): MemberSignUpResponse {
        if (memberRepository.existsByEmail(request.email)) {
            throw MemberAlreadyExistsException()
        }

        val member = Member.of(request)
        memberRepository.save(member)
        return MemberSignUpResponse.of(member)
    }

    fun signIn(request: MemberSignInRequest): MemberSignInResponse {
        val member = memberRepository.findByNameAndEmail(request.name, request.email) ?: throw MemberNotFoundException()
        return MemberSignInResponse.of(member)
    }

    fun withdraw(id: Long) {
        val member = memberRepository.findById(id).orElseThrow { MemberNotFoundException() }
        memberRepository.delete(member)
    }
}
