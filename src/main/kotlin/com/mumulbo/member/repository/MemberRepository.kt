package com.mumulbo.member.repository

import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): Member?
}
