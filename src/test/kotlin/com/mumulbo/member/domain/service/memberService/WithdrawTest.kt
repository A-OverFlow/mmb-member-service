package com.mumulbo.member.domain.service.memberService

import com.mumulbo.member.config.TestContainers
import com.mumulbo.member.domain.model.entity.Member
import com.mumulbo.member.domain.model.exception.MemberNotFoundException
import com.mumulbo.member.domain.model.repository.MemberRepository
import com.mumulbo.member.domain.service.MemberService
import kotlin.test.assertFalse
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class WithdrawTest : TestContainers() {
    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @AfterEach
    fun cleansing() {
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("회원탈퇴 성공")
    @Test
    fun `withdraw succeeded`() {
        // given
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        val member = memberRepository.save(Member(name, email))

        // when
        memberService.withdraw(member.id!!)

        // then
        assertFalse { memberRepository.existsById(member.id!!) }
    }

    @DisplayName("회원탈퇴 실패 - 존재하지 않는 사용자")
    @Test
    fun `withdraw failed - member not found`() {
        // given
        val name = "Joon Hee Song"
        val email = "joonhee.song@ahnlab.com"
        memberRepository.save(Member(name, email))

        // when // then
        assertThatThrownBy { memberService.withdraw(Long.MAX_VALUE) }
            .isInstanceOf(MemberNotFoundException::class.java)
    }
}
