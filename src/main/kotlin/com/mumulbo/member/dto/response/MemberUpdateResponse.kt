package com.mumulbo.member.dto.response

import com.mumulbo.member.entity.Member

class MemberUpdateResponse(
    val name: String,
    val username: String
) {
    companion object {
        fun of(member: Member): MemberUpdateResponse {
            return MemberUpdateResponse(member.name, member.username)
        }
    }
}
