package com.mumulbo.profile.dto.request

import java.util.Optional

data class ProfileInfoUpdateRequest(
    val nickname: String? = null,
    val introduction: Optional<String>?,
    val website: Optional<String>?
)
