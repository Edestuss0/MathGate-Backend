package com.backend.mathgate.repositories

import com.backend.mathgate.entities.LessonBlockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LessonBlocksRepository : JpaRepository<LessonBlockEntity, Int> {
    fun deleteAllByPageId(pageId: Int)

    fun findAllByPageId(pageId: Int): List<LessonBlockEntity>

    @Query("SELECT b FROM LessonBlockEntity b WHERE b.page.id IN :pageIds ORDER BY b.orderIndex")
    fun findAllByPageIdIn(pageIds: List<Int>):List<LessonBlockEntity>
}