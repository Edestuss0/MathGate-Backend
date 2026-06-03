package com.backend.mathgate.entities

import com.backend.mathgate.dto.BlockType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "blocks")
data class BlockEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false)
    val question: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: BlockType,

    @Column(nullable = false)
    val content: String,
)