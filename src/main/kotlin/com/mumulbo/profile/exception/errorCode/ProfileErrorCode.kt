package com.mumulbo.profile.exception.errorCode

import com.mumulbo.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class ProfileErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : ErrorCode {
    INVALID_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "PROFILE-001", "지원하지 않는 파일입니다.")
}
