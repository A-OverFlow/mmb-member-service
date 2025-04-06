package com.mumulbo.common.response

import com.mumulbo.common.exception.ErrorCode
import java.time.Clock
import java.time.LocalDateTime
import org.springframework.http.HttpStatus

class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String
) {
    constructor(httpStatus: HttpStatus, errorCode: String, message: String) : this(
        timestamp = LocalDateTime.now(Clock.systemDefaultZone()),
        status = httpStatus.value(),
        error = errorCode,
        message = message
    )

    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(
                LocalDateTime.now(Clock.systemDefaultZone()),
                errorCode.status.value(),
                errorCode.code,
                errorCode.message
            )
        }
    }
}
