package com.mumulbo.auth.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberSignUpRequest
import com.mumulbo.member.entity.Member
import com.mumulbo.member.exception.MemberAlreadyExistsException
import com.mumulbo.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class SignUpTest : TestContainers() {
    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("회원가입 성공")
    @Test
    fun `sign up succeeded`() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        val password = "password"
        val request = MemberSignUpRequest(name, email, username, password)

        // when
        val response = authService.signUp(request)

        // then
        assertThat(response)
            .extracting("name", "email", "username")
            .containsSequence(name, email, username)
    }

    @DisplayName("회원가입 실패 - 이미 가입한 사용자")
    @Test
    fun `sign up failed - member already exists`() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        val password = "encoded password"
        memberRepository.save(Member(name, email, username, password))

        val request = MemberSignUpRequest(name, email, username, password)

        // when // then
        assertThatThrownBy { authService.signUp(request) }
            .isInstanceOf(MemberAlreadyExistsException::class.java)
    }
}
