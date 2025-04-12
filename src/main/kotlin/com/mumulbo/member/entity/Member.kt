package com.mumulbo.member.entity

import com.mumulbo.member.dto.request.MemberCreateRequest
import com.mumulbo.member.enums.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
class Member(
    @field:Column
    val name: String,

    @field:Column
    val email: String,

    @field:Column
    val username: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    val role: Role = Role.MEMBER

    companion object {
        fun of(request: MemberCreateRequest) =
            Member(request.name, request.email, request.username)
    }
}
