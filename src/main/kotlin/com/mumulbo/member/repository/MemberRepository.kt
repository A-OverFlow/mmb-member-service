package com.mumulbo.member.repository

import com.mumulbo.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByNameAndEmail(name: String, email: String): Member?

    fun existsByEmail(email: String): Boolean
}
