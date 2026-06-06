package com.backend.mathgate.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(
    name = "lesson_pages",
    indexes = [Index(name = "idx_lesson_page_lesson", columnList = "lesson_id")]
)
data class LessonPageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "lesson_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    val lesson: LessonEntity,

    @Column(name = "order_index", nullable = false)
    val orderIndex: Int,

    @OneToMany(mappedBy = "page", fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    val blocks: Set<LessonBlockEntity> = LinkedHashSet(),
) {
    override fun equals(other: Any?): Boolean = other is LessonPageEntity && id == other.id
    override fun hashCode(): Int = id ?: 0
}