package com.backend.mathgate.repositories

import com.backend.mathgate.entities.LessonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LessonsRepository : JpaRepository<LessonEntity, Int> {
    @Query("SELECT l FROM LessonEntity l WHERE l.theme = :themeId")
    fun getAllByThemeId(themeId: Int): List<LessonEntity>
}