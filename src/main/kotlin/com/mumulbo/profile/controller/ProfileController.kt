package com.mumulbo.profile.controller

import com.mumulbo.profile.dto.response.ProfileGetResponse
import com.mumulbo.profile.dto.response.ProfilePictureUpdateResponse
import com.mumulbo.profile.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/members/me/profile")
class ProfileController(
    private val profileService: ProfileService
) {
    @GetMapping
    fun getProfile(@RequestHeader("X-User-Id") id: Long): ResponseEntity<ProfileGetResponse> {
        val response = profileService.getProfile(id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/picture")
    fun updatePicture(
        @RequestHeader("X-User-Id") id: Long,
        @RequestPart file: MultipartFile
    ): ResponseEntity<ProfilePictureUpdateResponse> {
        val response = profileService.updatePicture(id, file)
        return ResponseEntity.ok(response)
    }
}
