package com.mumulbo.profile.exception

import com.mumulbo.profile.exception.errorCode.ProfileErrorCode

class InvalidFileException : ProfileException(ProfileErrorCode.INVALID_TYPE)
