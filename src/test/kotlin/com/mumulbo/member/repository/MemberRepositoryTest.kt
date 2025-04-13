package com.mumulbo.member.repository

import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
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
class MemberRepositoryTest : TestContainers() {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun init() {
        // given
        val name = "송준희"
        val email = "joonhee.song@ahnlab.com"
        val username = "joonhee.song"
        memberRepository.save(Member(name, email, username))
    }

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("성공-existsByUsername")
    @Test
    fun `success-existsByEmail`() {
        // when
        val isExist = memberRepository.existsByEmail("joonhee.song@ahnlab.com")

        // then
        assertThat(isExist).isTrue()
    }

    @DisplayName("실패-existsByUsername")
    @Test
    fun `fail-existsByEmail`() {
        // when
        val isExist = memberRepository.existsByEmail("anonymous@ahnlab.com")

        // then
        assertThat(isExist).isFalse()
    }

    @DisplayName("성공-findByEmail")
    @Test
    fun `success-findByEmail`() {
        // when
        val member = memberRepository.findByEmail("joonhee.song@ahnlab.com")

        // then
        assertThat(member)
            .extracting("name", "email", "username")
            .contains("송준희", "joonhee.song@ahnlab.com", "joonhee.song")
    }

    @DisplayName("실패-findByUsername")
    @Test
    fun `fail-findByEmail`() {
        // when
        val member = memberRepository.findByEmail("anonymous@ahnlab.com")

        // then
        assertThat(member).isNull()
    }
}
