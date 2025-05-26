package com.mumulbo.profile.dto.request

import java.util.Optional

data class ProfileInfoUpdateRequest(
    val introduction: Optional<String>?,
    val website: Optional<String>?
)
