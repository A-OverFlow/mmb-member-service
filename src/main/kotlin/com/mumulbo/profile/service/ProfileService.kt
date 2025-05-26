package com.mumulbo.profile.service

import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.ProfileRepository
import com.mumulbo.profile.dto.request.ProfileInfoUpdateRequest
import com.mumulbo.profile.dto.response.ProfileGetResponse
import com.mumulbo.profile.dto.response.ProfileInfoUpdateResponse
import com.mumulbo.profile.dto.response.ProfilePictureUpdateResponse
import com.mumulbo.profile.entity.Profile
import java.net.URI
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

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

    fun updatePicture(id: Long, picture: MultipartFile): ProfilePictureUpdateResponse {
        val member = memberRepository.findWithProfileById(id) ?: throw MemberNotFoundException()
        val profile = member.profile

        val objectName = fileService.uploadImage(picture)
        profile.picture = objectName

        return ProfilePictureUpdateResponse("$bucket/profiles/${objectName}")
    }

    fun updateInfo(id: Long, request: ProfileInfoUpdateRequest): ProfileInfoUpdateResponse {
        val member = memberRepository.findWithProfileById(id) ?: throw MemberNotFoundException()
        val profile = member.profile

        request.introduction?.let { profile.introduction = it.orElse(null) }
        request.website?.let { profile.website = it.orElse(null) }

        return ProfileInfoUpdateResponse(profile)
    }
}
