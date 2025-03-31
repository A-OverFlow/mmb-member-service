package com.mumulbo.member.domain.model.exception

import com.mumulbo.member.domain.model.exception.errorCode.MemberErrorCode
import org.springframework.http.HttpStatus

open class MemberException(
    val status: HttpStatus,
    errorCode: MemberErrorCode,
    message: String
) : RuntimeException(message) {
    val errorCode = "MEMBER-${errorCode.code}"
}
