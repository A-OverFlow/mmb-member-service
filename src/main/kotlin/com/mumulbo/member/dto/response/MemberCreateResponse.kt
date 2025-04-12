package com.mumulbo.member.dto.response

import com.mumulbo.member.entity.Member

class MemberCreateResponse(
    val id: Long,
    val name: String,
    val email: String,
    val username: String
) {
    companion object {
        fun of(member: Member): MemberCreateResponse {
            return MemberCreateResponse(member.id!!, member.name, member.email, member.username)
        }
    }
}
