package com.mumulbo.member.domain.service.memberService

import com.mumulbo.member.application.request.MemberSignInRequest
import com.mumulbo.member.config.TestContainers
import com.mumulbo.member.domain.model.entity.Member
import com.mumulbo.member.domain.model.exception.MemberNotFoundException
import com.mumulbo.member.domain.model.repository.MemberRepository
import com.mumulbo.member.domain.service.MemberService
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
class SignInTest : TestContainers() {
    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun init() {
        // given
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        memberRepository.save(Member(name, email))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("로그인 성공")
    @Test
    fun `sign in succeeded`() {
        // given
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val request = MemberSignInRequest(name, email)

        // when
        val response = memberService.signIn(request)

        // then
        assertThat(response)
            .extracting("name", "email")
            .contains(name, email)
    }

    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    @Test
    fun `sign in failed-member not found`() {
        // given
        val name = "anonymous"
        val email = "anonymous@ahnlab.com"
        val request = MemberSignInRequest(name, email)

        // when //then
        assertThatThrownBy { memberService.signIn(request) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }
}
