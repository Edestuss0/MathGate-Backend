package com.backend.mathgate.entities

import com.backend.mathgate.dto.LessonBlockType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.type.SqlTypes

@Entity
@Table(
    name = "lesson_blocks",
    indexes = [Index(name = "idx_lesson_blocks_page_id", columnList = "page_id")]
)
data class LessonBlockEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "page_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val page: LessonPageEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "block_type")
    val blockType: LessonBlockType,

    @Column(name = "order_index", nullable = false)
    val orderIndex: Int,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    val payload: String,
)