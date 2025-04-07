package com.mumulbo.auth.controller

import com.mumulbo.auth.dto.request.RefreshTokenRequest
import com.mumulbo.auth.dto.response.TokenResponse
import com.mumulbo.auth.service.AuthService
import com.mumulbo.member.dto.request.MemberSignInRequest
import com.mumulbo.member.dto.request.MemberSignUpRequest
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
    fun signIn(@Valid @RequestBody request: MemberSignInRequest): ResponseEntity<TokenResponse> {
        val response = authService.signIn(request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/refresh-token")
    fun reissue(request: RefreshTokenRequest): ResponseEntity<TokenResponse> {
        val response = authService.refreshToken(request.refreshToken)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
