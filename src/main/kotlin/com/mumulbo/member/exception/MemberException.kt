package com.mumulbo.member.exception

import com.mumulbo.common.exception.GlobalException
import com.mumulbo.member.exception.errorcode.MemberErrorCode

open class MemberException(error: MemberErrorCode) : GlobalException(error)
