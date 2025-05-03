package com.mumulbo.member.service

import com.mumulbo.member.dto.MemberDto
import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.dto.response.MemberCreateOrGetResponse
import com.mumulbo.member.dto.response.MemberUpdateResponse
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun createOrGetMember(request: MemberCreateOrGetRequest): MemberCreateOrGetResponse {
        val member = memberRepository.findByProviderAndProviderId(request.provider, request.providerId)
            ?: memberRepository.save(Member.of(request))
        return MemberCreateOrGetResponse(member.id!!)
    }

    fun getMember(email: String): MemberDto {
        val member = (memberRepository.findByEmail(email)
            ?: throw MemberNotFoundException())
        return MemberDto.toDto(member)
    }

    fun getMember(id: Long): MemberDto {
        val member = memberRepository.findById(id)
            .orElseThrow { MemberNotFoundException() }

        return MemberDto.toDto(member)
    }

    fun updateMember(id: Long, request: MemberUpdateRequest): MemberUpdateResponse {
        val member = memberRepository.findById(id).orElseThrow { MemberNotFoundException() }
        member.update(request)
        memberRepository.save(member)
        return MemberUpdateResponse.of(member)
    }

    fun deleteMember(id: Long) {
        val member = memberRepository.findById(id).orElseThrow { MemberNotFoundException() }
        memberRepository.delete(member)
    }
}
