package com.mumulbo.profile.dto.response

import com.mumulbo.member.entity.Member

class ProfileGetResponse(
    val name: String,
    val email: String,
    val picture: String,
    val introduction: String?,
    val website: String?
) {
    constructor(member: Member) : this(
        name = member.name,
        email = member.email,
        picture = member.profile.picture,
        introduction = member.profile.introduction,
        website = member.profile.website
    )
}
