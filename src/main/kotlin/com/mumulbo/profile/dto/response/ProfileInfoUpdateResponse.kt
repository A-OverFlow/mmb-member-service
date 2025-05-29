package com.mumulbo.profile.dto.response

import com.mumulbo.profile.entity.Profile

data class ProfileInfoUpdateResponse(
    val nickname: String,
    val introduction: String?,
    val website: String?
) {
    constructor(profile: Profile) : this(profile.nickname, profile.introduction, profile.website)
}
