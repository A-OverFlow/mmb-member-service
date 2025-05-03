package com.mumulbo.member.dto.response

import com.mumulbo.member.entity.Member

class MemberCreateResponse(
    val id: Long,
    val name: String,
    val email: String,
    val profile: String
) {
    companion object {
        fun of(member: Member): MemberCreateResponse {
            return MemberCreateResponse(member.id!!, member.name, member.email, member.profile)
        }
    }
}
