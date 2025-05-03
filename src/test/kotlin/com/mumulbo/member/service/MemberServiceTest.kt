package com.mumulbo.member.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberCreateRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.enums.Provider
import com.mumulbo.member.exception.MemberNotFoundException
import com.mumulbo.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
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
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        member = memberRepository.save(Member(provider, providerId, name, email, profile))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("성공-createMember")
    @Test
    fun `success-createMember`() {
        // given
        val provider = Provider.GOOGLE
        val providerId = "012345678901234567890"
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val profile = "https://lh3.googleusercontent.com/a/ACg8ocLMTF71D62J-rh67V_H4T61l09FpgpHwepfAPy0VFTSd9bwSg=s96-c"
        val request = MemberCreateRequest(provider, providerId, name, email, profile)

        // when
        val response = memberService.createMember(request)

        // then
        assertThat(response)
            .extracting("name", "email")
            .contains(request.name, request.email)
    }

    @DisplayName("성공-checkMember")
    @Test
    fun `success-checkMember`() {
        // given
        val email = member.email

        // when
        val response = memberService.checkMember(email)

        // then
        assertThat(response.id).isEqualTo(member.id)
    }

    @DisplayName("실패-checkMember")
    @Test
    fun `fail-checkMember`() {
        // given
        val email = "anonymous@ahnlab.com"

        // when // then
        assertThatThrownBy { memberService.checkMember(email) }
            .isInstanceOf(MemberNotFoundException::class.java)
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
            .extracting("name", "email")
            .contains(member.name, member.email)
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

    @DisplayName("성공-updateMember")
    @Test
    fun `success-updateMember`() {
        // given
        val name = "송준희2"
        val request = MemberUpdateRequest(name)

        // when
        val response = memberService.updateMember(member.id!!, request)

        // then
        assertThat(response.name).isEqualTo(name)
    }

    @DisplayName("실패-updateMember")
    @Test
    fun `fail-updateMember`() {
        // given
        val name = "송준희2"
        val request = MemberUpdateRequest(name)

        // when // then
        assertThatThrownBy { memberService.updateMember(999_999L, request) }
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
