package com.mumulbo.member.exception

import com.mumulbo.member.exception.errorcode.MemberErrorCode
import org.springframework.http.HttpStatus

class MemberNotFoundException : MemberException(
    status = HttpStatus.NOT_FOUND,
    errorCode = MemberErrorCode.NOT_FOUND,
    message = "존재하지 않는 사용자입니다."
)
