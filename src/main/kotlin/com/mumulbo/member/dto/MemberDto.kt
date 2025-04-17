package com.mumulbo.member.dto

import com.mumulbo.member.entity.Member

data class MemberDto (
    val id: Long? = null,
    val name: String,
    val email: String,
    val username: String
) {
    companion object {
        fun toDto(member: Member): MemberDto {
            return MemberDto(
                id = member.id,
                name = member.name,
                email = member.email,
                username = member.username
            )
        }
    }
}