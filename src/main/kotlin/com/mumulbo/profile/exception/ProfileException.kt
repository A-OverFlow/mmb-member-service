package com.mumulbo.profile.exception

import com.mumulbo.common.exception.GlobalException
import com.mumulbo.profile.exception.errorCode.ProfileErrorCode

open class ProfileException(error: ProfileErrorCode) : GlobalException(error)
