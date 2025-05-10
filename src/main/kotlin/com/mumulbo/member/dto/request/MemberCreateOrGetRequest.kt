package com.mumulbo.member.dto.request

import com.mumulbo.member.enums.Provider

class MemberCreateOrGetRequest(
    val provider: Provider,
    val providerId: String,
    val name: String,
    val email: String,
    val profile: String
)
