package com.mumulbo.member.domain.model.exception

import com.mumulbo.member.domain.model.exception.errorCode.MemberErrorCode
import org.springframework.http.HttpStatus

class MemberNotFoundException : MemberException(
    status = HttpStatus.NOT_FOUND,
    errorCode = MemberErrorCode.NOT_FOUND,
    message = "존재하지 않는 사용자입니다."
)
