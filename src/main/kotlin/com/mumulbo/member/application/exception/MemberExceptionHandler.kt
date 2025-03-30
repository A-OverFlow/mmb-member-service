package com.mumulbo.member.application.exception

import com.mumulbo.member.common.response.ErrorResponse
import com.mumulbo.member.domain.model.exception.MemberException
import com.mumulbo.member.domain.model.exception.MemberNotFoundException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class MemberExceptionHandler {
    @ExceptionHandler(MemberNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: MemberException) =
        ErrorResponse(exception.status, exception.errorCode, exception.message!!)
}
