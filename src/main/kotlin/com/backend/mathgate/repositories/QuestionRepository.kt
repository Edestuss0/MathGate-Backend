package com.backend.mathgate.repositories

import com.backend.mathgate.entities.QuestionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface QuestionRepository : JpaRepository<QuestionEntity, Int> {

    @Query(value = "SELECT * FROM questions ORDER BY random() LIMIT 1", nativeQuery = true)
    fun findRandomQuestion(): QuestionEntity?
}

