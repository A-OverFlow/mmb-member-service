package com.mumulbo.member.entity

import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.enums.Provider
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Clock
import java.time.LocalDateTime

@Entity
@Table
class Member(
    @field:Column
    @Enumerated(EnumType.STRING)
    val provider: Provider,

    @field:Column
    val providerId: String,

    @field:Column
    val name: String,

    @field:Column
    var email: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    val createdAt: LocalDateTime = LocalDateTime.now(Clock.systemDefaultZone())

    companion object {
        fun of(request: MemberCreateOrGetRequest) =
            Member(request.provider, request.providerId, request.name, request.email)
    }

    fun update(request: MemberUpdateRequest) {
        this.email = request.email
    }
}
