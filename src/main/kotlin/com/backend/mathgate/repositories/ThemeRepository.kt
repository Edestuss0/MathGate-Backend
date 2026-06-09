package com.backend.mathgate.repositories

import com.backend.mathgate.entities.ThemeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ThemeRepository : JpaRepository<ThemeEntity, Int> {
    fun getByGrade(grade: Int): List<ThemeEntity>

    @Query("""
        SELECT DISTINCT t FROM ThemeEntity t
        LEFT JOIN FETCH t.lessons l
        WHERE t.grade = :grade
        ORDER BY l.orderIndex ASC
    """)
    fun getByGradeWithLessons(grade: Int): List<ThemeEntity>
}