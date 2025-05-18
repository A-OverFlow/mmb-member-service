package com.mumulbo.member.repository

import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.profile.entity.Profile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@Testcontainers
@Transactional
class MemberRepositoryTest : TestContainers() {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    private lateinit var member: Member

    @BeforeEach
    fun init() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"

        val picture = "https://lh3.googleusercontent.com/a/abcdefg"
        val profile = Profile(picture)

        member = memberRepository.save(Member(provider, providerId, name, email, profile))
    }

    @DisplayName("성공-findByIdJoinProfile")
    @Test
    fun `success-findById`() {
        // given
        val id = member.id!!

        // when
        val member = memberRepository.findByIdJoinProfile(id)

        // then
        assertThat(member)
            .isNotNull
            .extracting("name", "email", "profile.picture")
            .contains("송준희", "mike.urssu@gmail.com", "https://lh3.googleusercontent.com/a/abcdefg")
    }

    @DisplayName("실패-findByIdJoinProfile")
    @Test
    fun `fail-findById`() {
        // given
        val id = 999_999L

        // when
        val member = memberRepository.findByIdJoinProfile(id)

        // then
        assertThat(member).isNull()
    }

    @DisplayName("성공-findByProviderAndProviderId")
    @Test
    fun `success-findByProviderAndProviderId`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"

        // when
        val member = memberRepository.findByProviderAndProviderId(provider, providerId)

        // then
        assertThat(member)
            .isNotNull
            .extracting("name", "email", "profile.picture")
            .contains("송준희", "mike.urssu@gmail.com", "https://lh3.googleusercontent.com/a/abcdefg")
    }

    @DisplayName("실패-findByProviderAndProviderId")
    @Test
    fun `fail-findByProviderAndProviderId`() {
        // given
        val provider = Provider.KAKAO
        val providerId = "012345678901234567890"

        // when
        val member = memberRepository.findByProviderAndProviderId(provider, providerId)

        // then
        assertThat(member).isNull()
    }
}
