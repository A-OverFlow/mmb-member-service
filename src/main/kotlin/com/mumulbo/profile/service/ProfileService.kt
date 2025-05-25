package com.mumulbo.profile.service

import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.ProfileRepository
import com.mumulbo.profile.dto.response.ProfileGetResponse
import com.mumulbo.profile.entity.Profile
import java.net.URI
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfileService(
    private val fileService: FileService,
    private val memberRepository: MemberRepository,
    private val profileRepository: ProfileRepository,
    @Value("\${minio.bucket}")
    private val bucket: String
) {
    fun saveProfile(picture: String): Profile {
        val file = fileService.urlToMultipartFile(URI.create(picture).toURL())
        val objectName = fileService.uploadImage(file)
        val profile = Profile(objectName)
        return profileRepository.save(profile)
    }

    fun getProfile(id: Long): ProfileGetResponse {
        val member = memberRepository.findWithProfileById(id) ?: throw MemberNotFoundException()
        return ProfileGetResponse(member, bucket)
    }
}
