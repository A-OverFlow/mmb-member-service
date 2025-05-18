package com.mumulbo.profile.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.entity.Profile
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@Transactional
class ProfileServiceTest : TestContainers() {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var profileService: ProfileService

    @DisplayName("성공-createProfile")
    @Test
    fun `success-createMember`() {
        // given
        val picture = "https://lh3.googleusercontent.com/a/abcdefg"

        // when
        val response = profileService.createProfile(picture)

        // then
        assertThat(response.id).isPositive()
    }

    @DisplayName("성공-getProfile")
    @Test
    fun `success-getProfile`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val picture = "https://lh3.googleusercontent.com/a/abcdefg"

        val profile = Profile(picture)
        val member = memberRepository.save(Member(provider, providerId, name, email, profile))

        val id = member.id!!

        // when
        val response = profileService.getProfile(id)

        // then
        assertThat(response).isNotNull
            .extracting("name", "email", "picture")
            .contains(name, email, picture)
    }

    @DisplayName("fail-getProfile")
    @Test
    fun `fail-getProfile`() {
        // given
        val id = 999_999L

        // when // then
        assertThatThrownBy { profileService.getProfile(id) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }
}
