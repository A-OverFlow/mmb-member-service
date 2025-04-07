package com.mumulbo.common.exception

import com.mumulbo.auth.exception.errorCode.AuthErrorCode

class UnauthorizedException : GlobalException(AuthErrorCode.UNAUTHORIZED)
