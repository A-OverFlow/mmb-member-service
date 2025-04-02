package com.mumulbo.member.exception.handler

import com.mumulbo.common.response.ErrorResponse
import com.mumulbo.member.exception.MemberAlreadyExistsException
import com.mumulbo.member.exception.MemberException
import com.mumulbo.member.exception.MemberNotFoundException
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

    @ExceptionHandler(MemberAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflict(exception: MemberException) =
        ErrorResponse(exception.status, exception.errorCode, exception.message!!)
}
