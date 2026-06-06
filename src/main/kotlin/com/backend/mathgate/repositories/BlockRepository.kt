package com.backend.mathgate.repositories

import com.backend.mathgate.entities.BlockEntity
import org.springframework.data.jpa.repository.JpaRepository


interface BlockRepository : JpaRepository<BlockEntity, Int> {
    fun findAllByQuestion(question: Int): List<BlockEntity>
    fun deleteAllByQuestion(question: Int)
}