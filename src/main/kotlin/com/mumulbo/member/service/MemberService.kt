package com.mumulbo.member.service

import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.dto.response.MemberCreateOrGetResponse
import com.mumulbo.member.dto.response.MemberGetResponse
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.service.ProfileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService(
    private val profileService: ProfileService,
    private val memberRepository: MemberRepository
) {
    fun createOrGetMember(request: MemberCreateOrGetRequest): MemberCreateOrGetResponse {
        val member = memberRepository.findByProviderAndProviderId(request.provider, request.providerId)
            ?: createMember(request)
        return MemberCreateOrGetResponse(member.id!!)
    }

    fun createMember(request: MemberCreateOrGetRequest): Member {
        val profile = profileService.createProfile(request.picture)
        val member = Member.of(request, profile)
        return memberRepository.save(member)
    }

    fun getMember(id: Long): MemberGetResponse {
        val member = memberRepository.findByIdJoinProfile(id) ?: throw MemberNotFoundException()
        return MemberGetResponse.of(member)
    }

    fun deleteMember(id: Long) {
        val member = memberRepository.findById(id).orElseThrow { MemberNotFoundException() }
        memberRepository.delete(member)
    }
}
