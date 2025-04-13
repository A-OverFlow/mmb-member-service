package com.mumulbo.member.service

import com.mumulbo.member.dto.request.MemberCheckRequest
import com.mumulbo.member.dto.request.MemberCreateRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.dto.response.MemberCheckResponse
import com.mumulbo.member.dto.response.MemberCreateResponse
import com.mumulbo.member.dto.response.MemberGetResponse
import com.mumulbo.member.dto.response.MemberUpdateResponse
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberAlreadyExistsException
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun createMember(request: MemberCreateRequest): MemberCreateResponse {
        if (memberRepository.existsByEmail(request.email)) {
            throw MemberAlreadyExistsException()
        }

        val member = Member.of(request)
        memberRepository.save(member)
        return MemberCreateResponse.of(member)
    }

    fun checkMember(request: MemberCheckRequest): MemberCheckResponse {
        val member = memberRepository.findByEmail(request.email) ?: throw MemberNotFoundException()
        return MemberCheckResponse(member.id!!)
    }

    fun getMember(id: Long): MemberGetResponse {
        val member = memberRepository.findById(id).orElseThrow { MemberNotFoundException() }
        return MemberGetResponse.of(member)
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
