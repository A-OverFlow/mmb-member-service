package com.mumulbo.member.controller

import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.dto.response.MemberCreateOrGetResponse
import com.mumulbo.member.dto.response.MemberGetResponse
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService
) {
    @PostMapping
    fun createOrGetMember(@RequestBody request: MemberCreateOrGetRequest): ResponseEntity<MemberCreateOrGetResponse> {
        val response = memberService.createOrGetMember(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/me")
    fun getMyInfo(@RequestHeader("X-User-Id") id: Long): ResponseEntity<MemberGetResponse> {
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
