package com.mumulbo.member.controller

import com.mumulbo.config.TestContainers
import com.mumulbo.member.entity.Member
import com.mumulbo.member.repository.MemberRepository
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class WithdrawTest : TestContainers() {
    @Autowired
    private lateinit var mockMvc: MockMvc

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
        val member = memberRepository.save(Member(name, email, "", ""))

        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/members/{id}", member.id)
        )
            .andExpect(status().isNoContent)
    }

    @DisplayName("회원탈퇴 실패 - 존재하지 않는 사용자")
    @Test
    fun `withdraw failed - member not found`() {
        // when // then
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("/api/v1/members/{id}", Long.MAX_VALUE)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status", `is`(HttpStatus.NOT_FOUND.value())))
            .andExpect(jsonPath("$.error", `is`("MEMBER-001")))
            .andExpect(jsonPath("$.message", `is`("존재하지 않는 사용자입니다.")))
    }
}
