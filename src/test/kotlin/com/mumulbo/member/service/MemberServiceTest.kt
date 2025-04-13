package com.mumulbo.member.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberCheckRequest
import com.mumulbo.member.dto.request.MemberCreateRequest
import com.mumulbo.member.dto.request.MemberUpdateRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberAlreadyExistsException
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
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        member = memberRepository.save(Member(name, email, username))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("성공-createMember")
    @Test
    fun `success-createMember`() {
        // given
        val request = MemberCreateRequest("송준희2", "joonhee.song2@ahnlab.com", "joonhee.song2")

        // when
        val response = memberService.createMember(request)

        // then
        assertThat(response)
            .extracting("name", "email", "username")
            .contains(request.name, request.email, request.username)
    }

    @DisplayName("실패-createMember")
    @Test
    fun `fail-createMember`() {
        // given
        val request = MemberCreateRequest("송준희", "joonhee.song@ahnlab.com", "joonhee.song")

        // when // then
        assertThatThrownBy { memberService.createMember(request) }
            .isInstanceOf(MemberAlreadyExistsException::class.java)
    }

    @DisplayName("성공-checkMember")
    @Test
    fun `success-checkMember`() {
        // given
        val request = MemberCheckRequest(member.email)

        // when
        val response = memberService.checkMember(request)

        // then
        assertThat(response.id).isEqualTo(member.id)
    }

    @DisplayName("실패-checkMember")
    @Test
    fun `fail-checkMember`() {
        // given
        val request = MemberCheckRequest("anonymous@ahnlab.com")

        // when // then
        assertThatThrownBy { memberService.checkMember(request) }
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
            .extracting("name", "email", "username")
            .contains(member.name, member.email, member.username)
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
        val username = "joonhee.song2"
        val request = MemberUpdateRequest(name, username)

        // when
        val response = memberService.updateMember(member.id!!, request)

        // then
        assertThat(response)
            .extracting("name", "username")
            .contains(name, username)
    }

    @DisplayName("실패-updateMember")
    @Test
    fun `fail-updateMember`() {
        // given
        val name = "송준희2"
        val username = "joonhee.song2"
        val request = MemberUpdateRequest(name, username)

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
