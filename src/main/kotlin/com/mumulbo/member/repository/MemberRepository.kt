package com.mumulbo.member.repository

import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    @Query(
        """
        select member from Member member
        join fetch member.profile
        where member.id = :id
    """
    )
    fun findByIdJoinProfile(id: Long): Member?

    fun findByProviderAndProviderId(provider: Provider, providerId: String): Member?
}
