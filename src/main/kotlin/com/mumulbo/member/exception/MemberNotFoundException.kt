package com.mumulbo.member.exception

import com.mumulbo.member.exception.errorcode.MemberErrorCode

class MemberNotFoundException : MemberException(MemberErrorCode.NOT_FOUND)
