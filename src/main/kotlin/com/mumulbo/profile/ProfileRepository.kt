package com.mumulbo.profile

import com.mumulbo.profile.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository : JpaRepository<Profile, Long>
