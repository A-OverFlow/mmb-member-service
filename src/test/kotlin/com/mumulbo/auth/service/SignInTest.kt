package com.mumulbo.auth.service

import com.mumulbo.config.TestContainers
import com.mumulbo.member.dto.request.MemberSignInRequest
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
import org.springframework.security.crypto.password.PasswordEncoder
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class SignInTest : TestContainers() {
    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun init() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        val password = passwordEncoder.encode("password")
        memberRepository.save(Member(name, email, username, password))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("로그인 성공")
    @Test
    fun `success-sign in`() {
        // given
        val username = "joonhee.song"
        val password = "password"
        val request = MemberSignInRequest(username, password)

        // when
        val response = authService.signIn(request)

        // then
        assertThat(response.refreshToken).isNotNull()
        assertThat(response.accessToken).isNotNull()
    }

    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    @Test
    fun `fail-member not found`() {
        // given
        val username = "anonymous"
        val password = "anonymous"
        val request = MemberSignInRequest(username, password)

        // when //then
        assertThatThrownBy { authService.signIn(request) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }
}
