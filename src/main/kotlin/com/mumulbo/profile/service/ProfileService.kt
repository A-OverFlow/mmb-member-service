package com.mumulbo.profile.service

import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.ProfileRepository
import com.mumulbo.profile.dto.response.ProfileGetResponse
import com.mumulbo.profile.entity.Profile
import org.springframework.stereotype.Service

@Service
class ProfileService(
    private val memberRepository: MemberRepository,
    private val profileRepository: ProfileRepository
) {
    fun createProfile(picture: String): Profile {
        val profile = Profile(picture)
        return profileRepository.save(profile)
    }

    fun getProfile(id: Long): ProfileGetResponse {
        val member = memberRepository.findByIdJoinProfile(id) ?: throw MemberNotFoundException()
        return ProfileGetResponse(member)
    }
}
