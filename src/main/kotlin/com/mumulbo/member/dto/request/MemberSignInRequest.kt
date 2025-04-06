package com.mumulbo.member.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class MemberSignInRequest(
    @field:NotBlank(message = "아이디는 필수입니다.")
    @field:Size(max = 20, message = "아이디는 최대 20자까지 가능합니다.")
    val username: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(max = 20, message = "이메일은 최대 20자까지 가능합니다.")
    val password: String
)
