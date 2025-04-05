package com.mumulbo.auth.controller

import com.mumulbo.auth.service.AuthService
import com.mumulbo.member.dto.request.MemberSignInRequest
import com.mumulbo.member.dto.request.MemberSignUpRequest
import com.mumulbo.member.dto.response.MemberSignInResponse
import com.mumulbo.member.dto.response.MemberSignUpResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/sign-up")
    fun signUp(@Valid @RequestBody request: MemberSignUpRequest): ResponseEntity<MemberSignUpResponse> {
        val response = authService.signUp(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @PostMapping("/sign-in")
    fun signIn(@Valid @RequestBody request: MemberSignInRequest): ResponseEntity<MemberSignInResponse> {
        val response = authService.signIn(request)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
