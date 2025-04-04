package com.mumulbo.common.response

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
}
