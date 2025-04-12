package com.mumulbo.common.response

import com.mumulbo.common.exception.ErrorCode

class ErrorResponse(
    val code: String,
    val message: String
) {
    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(
                errorCode.code,
                errorCode.message
            )
        }
    }
}
