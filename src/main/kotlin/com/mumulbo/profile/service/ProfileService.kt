package com.mumulbo.profile.service

import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.ProfileRepository
import com.mumulbo.profile.dto.response.ProfileGetResponse
import com.mumulbo.profile.entity.Profile
import java.net.URI
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfileService(
    private val fileService: FileService,
    private val memberRepository: MemberRepository,
    private val profileRepository: ProfileRepository
) {
    fun saveProfile(picture: String): Profile {
        val file = fileService.urlToMultipartFile(URI.create(picture).toURL())
        val uploadedPicture = fileService.uploadImage(file)
        val profile = Profile(uploadedPicture)
        return profileRepository.save(profile)
    }

    fun createProfile(picture: String): Profile {
        val profile = Profile(picture)
        return profileRepository.save(profile)
    }

    fun getProfile(id: Long): ProfileGetResponse {
        val member = memberRepository.findByIdJoinProfile(id) ?: throw MemberNotFoundException()
        return ProfileGetResponse(member)
    }
}
