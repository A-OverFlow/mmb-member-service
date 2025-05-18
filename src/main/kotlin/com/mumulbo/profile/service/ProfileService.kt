package com.mumulbo.profile.service

import com.mumulbo.profile.ProfileRepository
import com.mumulbo.profile.entity.Profile
import org.springframework.stereotype.Service

@Service
class ProfileService(
    private val profileRepository: ProfileRepository
) {
    fun createProfile(picture: String): Profile {
        val profile = Profile(picture)
        return profileRepository.save(profile)
    }
}
