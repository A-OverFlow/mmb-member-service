package com.mumulbo.member.exception.errorcode

import com.mumulbo.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class MemberErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-001", "존재하지 않는 회원입니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER-002", "이미 가입한 회원입니다.")
}
