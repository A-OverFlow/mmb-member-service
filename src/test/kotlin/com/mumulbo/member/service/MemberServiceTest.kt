package com.mumulbo.member.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
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
        val password = "password"
        member = memberRepository.save(Member(name, email, username, password))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("성공-회원 정보 조회")
    @Test
    fun `success-get member`() {
        // when
        val response = memberService.getMember(member.id!!)

        // then
        assertThat(response)
            .extracting("name", "email", "username")
            .contains(member.name, member.email, member.username)
    }

    @DisplayName("실패-존재하지 않는 회원")
    @Test
    fun test() {
        // when // then
        assertThatThrownBy { memberService.getMember(999_999L) }
            .isInstanceOf(MemberNotFoundException::class.java)

    }
}
