package com.backend.mathgate.repositories

import com.backend.mathgate.entities.LessonEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LessonsRepository : JpaRepository<LessonEntity, Int> {
    fun getAllByThemeId(themeId: Int): List<LessonEntity>
}