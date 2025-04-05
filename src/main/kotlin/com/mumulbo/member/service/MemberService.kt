package com.mumulbo.member.service

import com.mumulbo.member.dto.request.MemberSignInRequest
import com.mumulbo.member.dto.response.MemberSignInResponse
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun signIn(request: MemberSignInRequest): MemberSignInResponse {
        val member = memberRepository.findByNameAndEmail(request.name, request.email) ?: throw MemberNotFoundException()
        return MemberSignInResponse.of(member)
    }

    fun withdraw(id: Long) {
        val member = memberRepository.findById(id).orElseThrow { MemberNotFoundException() }
        memberRepository.delete(member)
    }
}
