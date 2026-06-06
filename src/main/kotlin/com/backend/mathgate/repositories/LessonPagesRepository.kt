package com.backend.mathgate.repositories

import com.backend.mathgate.entities.LessonPageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LessonPagesRepository : JpaRepository<LessonPageEntity, Int> {
    fun deleteAllByLessonId(lessonId: Int)

    fun findAllByLessonId(lessonId: Int): List<LessonPageEntity>
}