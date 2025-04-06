package com.mumulbo.member.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class MemberSignUpRequest(
    @field:NotBlank(message = "이름은 필수입니다.")
    @field:Size(max = 100, message = "이름은 최대 100자까지 가능합니다.")
    val name: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:Size(max = 254, message = "이메일은 최대 254자까지 가능합니다.")
    val email: String,

    @field:NotBlank(message = "아이디는 필수입니다.")
    @field:Size(max = 20, message = "아이디는 최대 20자까지 가능합니다.")
    val username: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(max = 20, message = "비밀번호는 최대 20자까지 가능합니다.")
    val password: String
)
