package com.mumulbo.member.domain.model.entity

import com.mumulbo.member.application.request.MemberSignInRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    indexes = [Index(name = "member_uuid_idx", columnList = "uuid")],
    uniqueConstraints = [
        UniqueConstraint(name = "member_unique_email", columnNames = ["email"])
    ]
)
class Member(
    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, length = 254, unique = true)
    val email: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
