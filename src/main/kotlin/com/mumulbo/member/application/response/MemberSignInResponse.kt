package com.mumulbo.member.application.response

import com.mumulbo.member.domain.model.entity.Member

class MemberSignInResponse(
    val id: Long,
    val name: String,
    val email: String
) {
    companion object {
        fun of(member: Member) =
            MemberSignInResponse(member.id!!, member.name, member.email)
    }
}
