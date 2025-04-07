package com.mumulbo.member.entity

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
    @field:Column(nullable = false, length = 100)
    val name: String,

    @field:Column(nullable = false, length = 254)
    val email: String,

    @field:Column(nullable = false, length = 20, unique = true)
    val username: String,

    @field:Column(nullable = false, length = 60, unique = true)
    val password: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    val role: Role = Role.MEMBER

    companion object {
        fun of(name: String, email: String, username: String, password: String) =
            Member(name, email, username, password)
    }
}
