package com.mumulbo.common.exception

import com.mumulbo.common.response.ErrorResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GeneralExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRequestValid(exception: MethodArgumentNotValidException): ErrorResponse {
        val message = exception.bindingResult.fieldErrors.joinToString("") { fieldError ->
            "[${fieldError.field}](은)는 ${fieldError.defaultMessage}"
        }
        return ErrorResponse("SYSTEM-001", message)
    }

    @ExceptionHandler(GlobalException::class)
    fun handleCustomException(exception: GlobalException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(exception.errorCode)
        return ResponseEntity.status(exception.errorCode.status).body(errorResponse)
    }
}
