package com.mumulbo.profile

import com.mumulbo.config.TestContainers
import com.mumulbo.profile.service.ProfileService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class ProfileServiceTest : TestContainers() {
    @Autowired
    private lateinit var profileService: ProfileService

    @DisplayName("성공-createProfile")
    @Test
    fun `success-createMember`() {
        // given
        val picture = "https://lh3.googleusercontent.com/a/abcdefg"

        // when
        val response = profileService.createProfile(picture)

        // then
        assertThat(response.id).isPositive()
    }
}
