package com.mumulbo.member.dto.response

import com.mumulbo.member.entity.Member

class MemberGetResponse(
    val id: Long,
    val name: String,
    val email: String,
    val nickname: String,
    val picture: String
) {
    companion object {
        fun of(member: Member): MemberGetResponse {
            return MemberGetResponse(member.id!!, member.name, member.email, member.profile.nickname, "images/profiles/${member.profile.picture}")
        }
    }
}
