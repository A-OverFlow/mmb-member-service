package com.mumulbo.member.repository

import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
import kotlin.test.assertNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@Testcontainers
class FindByUsernameTest : TestContainers() {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun init() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        val password = "password"
        memberRepository.save(Member(name, email, username, password))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("username이 존재")
    @Test
    fun `success-member found`() {
        // when
        val member = memberRepository.findByUsername("joonhee.song")

        // then
        assertThat(member)
            .extracting("name", "email", "username")
            .contains("송준희", "joonhee.song@ahnlab.com", "joonhee.song")
    }

    @DisplayName("username이 존재하지 않음")
    @Test
    fun `fail-member not found`() {
        // when
        val member = memberRepository.findByUsername("anonymous")

        // then
        assertNull(member)
    }
}
