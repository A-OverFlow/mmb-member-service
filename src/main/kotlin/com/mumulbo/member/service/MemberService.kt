package com.mumulbo.member.service

import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun withdraw(id: Long) {
        val member = memberRepository.findById(id).orElseThrow { MemberNotFoundException() }
        memberRepository.delete(member)
    }
}
