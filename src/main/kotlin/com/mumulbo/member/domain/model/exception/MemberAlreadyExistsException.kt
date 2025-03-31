package com.mumulbo.member.domain.model.exception

import com.mumulbo.member.domain.model.exception.errorCode.MemberErrorCode
import org.springframework.http.HttpStatus

class MemberAlreadyExistsException : MemberException(
    status = HttpStatus.CONFLICT,
    errorCode = MemberErrorCode.ALREADY_EXISTS,
    message = "이미 가입한 사용자입니다."
)
