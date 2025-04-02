package com.mumulbo.member.exception

import com.mumulbo.member.exception.errorcode.MemberErrorCode
import org.springframework.http.HttpStatus

open class MemberException(
    val status: HttpStatus,
    errorCode: MemberErrorCode,
    message: String
) : RuntimeException(message) {
    val errorCode = "MEMBER-${errorCode.code}"
}
