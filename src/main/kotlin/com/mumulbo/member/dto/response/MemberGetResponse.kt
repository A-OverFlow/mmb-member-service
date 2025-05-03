package com.mumulbo.member.dto.response

import com.mumulbo.member.entity.Member

class MemberGetResponse(
    val name: String,
    val email: String
) {
    companion object {
        fun of(member: Member): MemberGetResponse {
            return MemberGetResponse(member.name, member.email)
        }
    }
}
