package com.mumulbo.member.entity

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

    companion object {
        fun of(name: String, email: String, username: String, password: String) =
            Member(name, email, username, password)
    }
}
