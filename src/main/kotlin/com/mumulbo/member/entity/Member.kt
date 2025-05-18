package com.mumulbo.member.entity

import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.enums.Provider
import com.mumulbo.profile.entity.Profile
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.time.Clock
import java.time.LocalDateTime

@Entity
class Member(
    @field:Column
    @Enumerated(EnumType.STRING)
    val provider: Provider,

    @field:Column
    val providerId: String,

    @field:Column
    val name: String,

    @field:Column
    var email: String,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    var profile: Profile
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    val createdAt: LocalDateTime = LocalDateTime.now(Clock.systemDefaultZone())

    companion object {
        fun of(request: MemberCreateOrGetRequest, profile: Profile) =
            Member(request.provider, request.providerId, request.name, request.email, profile)
    }
}
