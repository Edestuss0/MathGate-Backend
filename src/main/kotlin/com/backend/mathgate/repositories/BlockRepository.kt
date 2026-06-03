package com.backend.mathgate.repositories

import com.backend.mathgate.entities.BlockEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface BlockRepository : JpaRepository<BlockEntity, Int> {
    fun findAllByQuestion(question: Int): List<BlockEntity>
}