package com.mumulbo.member.exception.errorcode

import org.springframework.http.HttpStatus

enum class MemberErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "존재하지 않는 회원입니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "002", "이미 가입한 회원입니다.")
}
