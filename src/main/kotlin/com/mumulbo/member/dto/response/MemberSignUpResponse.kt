package com.mumulbo.member.dto.response

import com.mumulbo.member.entity.Member

class MemberSignUpResponse(
    val name: String,
    val email: String,
    val username: String
) {
    companion object {
        fun of(member: Member) =
            MemberSignUpResponse(member.name, member.email, member.username)
    }
}
