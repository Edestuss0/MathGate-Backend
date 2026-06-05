package com.backend.mathgate.repositories

import com.backend.mathgate.entities.LessonBlockEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LessonBlocksRepository : JpaRepository<LessonBlockEntity, Int> {
    fun getAllByPageId(pageId: Int): List<LessonBlockEntity>
}