package com.backend.mathgate.repositories

import com.backend.mathgate.entities.LessonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LessonsRepository : JpaRepository<LessonEntity, Int> {
    fun findAllByThemeId(themeId: Int): List<LessonEntity>

    @Query("""
        SELECT DISTINCT l FROM LessonEntity l
        LEFT JOIN FETCH l.pages p
        LEFT JOIN FETCH p.blocks b
        WHERE l.id = :id
        ORDER BY p.orderIndex, b.orderIndex
    """)
    fun findByIdWithPagesAndBlocks(id: Int): LessonEntity?

    @Query("""
        SELECT DISTINCT l FROM LessonEntity l
        LEFT JOIN FETCH l.pages p
        LEFT JOIN FETCH p.blocks b
        WHERE l.id in :ids
    """)
    fun findByIdsWithPagesAndBlocks(ids: List<Int>): List<LessonEntity>
}