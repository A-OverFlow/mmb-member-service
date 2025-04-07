package com.mumulbo.auth.exception

import com.mumulbo.auth.exception.errorCode.AuthErrorCode
import com.mumulbo.common.exception.GlobalException

class InvalidTokenException : GlobalException(AuthErrorCode.INVALID_TOKEN)
