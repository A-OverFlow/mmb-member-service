package com.mumulbo.profile.dto.response

import com.mumulbo.profile.entity.Profile

data class ProfileInfoUpdateResponse(
    val introduction: String?,
    val website: String?
) {
    constructor(profile: Profile) : this(profile.introduction, profile.website)
}
