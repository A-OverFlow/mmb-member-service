package com.mumulbo.common.exception

open class GlobalException(
    val errorCode: ErrorCode
) : RuntimeException()
