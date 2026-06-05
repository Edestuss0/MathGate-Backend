package com.backend.mathgate.repositories

import com.backend.mathgate.entities.LessonPageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LessonPagesRepository : JpaRepository<LessonPageEntity, Int> {
    fun getAllByLessonId(lessonId: Int): List<LessonPageEntity>
}