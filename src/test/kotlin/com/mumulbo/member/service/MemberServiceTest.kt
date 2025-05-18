package com.mumulbo.member.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberCreateOrGetRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import com.mumulbo.profile.entity.Profile
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@Transactional
class MemberServiceTest : TestContainers() {
    @Autowired
    private lateinit var memberService: MemberService

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

    @DisplayName("성공-createOrGetMember")
    @Test
    fun `success-createMember`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "송준희"
        val email = "mike.urssu@gmail.com"
        val picture = "https://lh3.googleusercontent.com/a/abcdefg"
        val request = MemberCreateOrGetRequest(provider, providerId, name, email, picture)

        // when
        val response = memberService.createOrGetMember(request)

        // then
        assertThat(response.id).isPositive()
    }

    @DisplayName("성공-getMember")
    @Test
    fun `success-getMember`() {
        // given
        val id = member.id!!

        // when
        val response = memberService.getMember(id)

        // then
        assertThat(response)
            .extracting("name", "email", "picture")
            .contains(member.name, member.email, member.profile.picture)
    }

    @DisplayName("실패-getMember")
    @Test
    fun `fail-getMember`() {
        // given
        val id = 999_999L

        // when // then
        assertThatThrownBy { memberService.getMember(id) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }

    @DisplayName("성공-deleteMember")
    @Test
    fun `success-deleteMember`() {
        // given
        val id = member.id!!

        // when
        memberService.deleteMember(id)

        // then
        assertThat(memberRepository.existsById(member.id!!)).isFalse()
    }

    @DisplayName("실패-deleteMember")
    @Test
    fun `fail-deleteMember`() {
        // given
        val id = 999_999L

        // when // then
        assertThatThrownBy { memberService.deleteMember(id) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }
}
