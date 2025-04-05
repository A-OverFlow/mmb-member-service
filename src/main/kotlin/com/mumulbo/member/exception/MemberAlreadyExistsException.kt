package com.mumulbo.member.exception

import com.mumulbo.member.exception.errorcode.MemberErrorCode

class MemberAlreadyExistsException : MemberException(MemberErrorCode.ALREADY_EXISTS)
