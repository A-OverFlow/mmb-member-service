package com.mumulbo.member.controller

import com.mumulbo.auth.exception.InvalidTokenException
import com.mumulbo.auth.service.JwtTokenProvider
import com.mumulbo.common.exception.UnauthorizedException
import com.mumulbo.member.dto.response.MemberGetResponse
import com.mumulbo.member.service.MemberService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @GetMapping("/me")
    fun getMember(request: HttpServletRequest): ResponseEntity<MemberGetResponse> {
        val header = request.getHeader("Authorization")
        if (header == null || !header.startsWith("Bearer ")) {
            throw UnauthorizedException()
        }

        val accessToken = header.substring(7)
        if (!jwtTokenProvider.isValid(accessToken)) {
            throw InvalidTokenException()
        }

        val id = jwtTokenProvider.getIdFromToken(accessToken)
        val response = memberService.getMember(id)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun withdraw(@PathVariable id: Long) =
        memberService.withdraw(id)
}
