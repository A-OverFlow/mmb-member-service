package com.mumulbo.member.domain.service

import com.mumulbo.member.application.request.MemberSignInRequest
import com.mumulbo.member.application.response.MemberSignInResponse
import com.mumulbo.member.domain.model.exception.MemberNotFoundException
import com.mumulbo.member.domain.model.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun signIn(request: MemberSignInRequest): MemberSignInResponse {
        val member = memberRepository.findByNameAndEmail(request.name, request.email) ?: throw MemberNotFoundException()
        return MemberSignInResponse.of(member)
    }
}
