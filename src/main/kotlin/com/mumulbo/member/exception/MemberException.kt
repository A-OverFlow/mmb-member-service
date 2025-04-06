package com.mumulbo.member.exception

import com.mumulbo.member.exception.errorcode.MemberErrorCode
import org.springframework.http.HttpStatus

open class MemberException(
    error: MemberErrorCode
) : RuntimeException(error.message) {
    val status: HttpStatus = error.status
    val errorCode: String = "MEMBER-${error.code}"
}
