package com.mumulbo.member.dto.response

import com.mumulbo.member.entity.Member

class MemberSignUpResponse(
    val id: Long,
    val name: String,
    val email: String
) {
    companion object {
        fun of(member: Member) =
            MemberSignUpResponse(member.id!!, member.name, member.email)
    }
}
