package com.mumulbo.profile.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
class Profile(
    @field:Column
    var nickname: String,

    @field:Column
    var picture: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column
    val id: Long? = null

    @field:Column
    var introduction: String? = null

    @field:Column
    var website: String? = null
}
