package com.mumulbo.auth.exception.errorCode

import com.mumulbo.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증되지 않은 요청입니다."),
    INVALID_TOKEN(HttpStatus.CONFLICT, "AUTH-002", "유효하지 않은 토큰입니다.")
}
