package com.mumulbo.member.entity

import com.mumulbo.member.dto.request.MemberSignUpRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
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
  
    companion object {
        fun of(request: MemberSignUpRequest) =
            Member(request.name, request.email)
    }
}
