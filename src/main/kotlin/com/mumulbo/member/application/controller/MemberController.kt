package com.mumulbo.member.application.controller

import com.mumulbo.member.application.request.MemberSignInRequest
import com.mumulbo.member.domain.service.MemberService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService
) {
    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    fun signIn(@Valid @RequestBody request: MemberSignInRequest) =
        memberService.signIn(request)
}
