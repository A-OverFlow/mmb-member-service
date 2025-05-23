package com.mumulbo.member.repository

import com.mumulbo.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): Member?
}
