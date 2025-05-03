package com.mumulbo.member.controller

import com.mumulbo.member.dto.MemberDto
import com.mumulbo.member.dto.request.MemberCreateRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.dto.response.MemberCheckResponse
import com.mumulbo.member.dto.response.MemberUpdateResponse
import com.mumulbo.member.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService
) {
    @GetMapping("/me")
    fun getMyInfo(@RequestHeader("X-User-Id") userId: String): ResponseEntity<MemberDto> {
        // todo 일단은 x-user-id가 이메일이지만 db에 저장된 user id로 변경해야함
        //  user id로 검색하는 함수가 필요함
        println("@GetMapping(\"/me\")")
        println(userId)
        return ResponseEntity.ok(memberService.getMember(userId))
    }

    @PostMapping
    fun createMember(@RequestBody request: MemberCreateRequest): ResponseEntity<MemberDto> {
        val response = memberService.createMember(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getMember(@RequestParam(name = "email") email: String): ResponseEntity<MemberDto> {
        return ResponseEntity.ok(memberService.getMember(email))
    }

    @GetMapping("/check")
    fun checkMember(@RequestParam email: String): ResponseEntity<MemberCheckResponse> {
        val response = memberService.checkMember(email)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getMember(@PathVariable id: Long): ResponseEntity<MemberDto> {
        val response = memberService.getMember(id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateMember(
        @PathVariable id: Long,
        @RequestBody request: MemberUpdateRequest
    ): ResponseEntity<MemberUpdateResponse> {
        val response = memberService.updateMember(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<Void> {
        memberService.deleteMember(id)
        return ResponseEntity.noContent().build()
    }
}
