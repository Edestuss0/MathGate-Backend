package com.backend.mathgate.repositories

import com.backend.mathgate.entities.ThemeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ThemeRepository : JpaRepository<ThemeEntity, Int> {
    fun getByGrade(grade: Int): List<ThemeEntity>
}