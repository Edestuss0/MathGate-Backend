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
    name = "lessons",
    indexes = [Index(name = "idx_lessons_theme_idx", columnList = "theme_id")]
)
data class LessonEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val theme: ThemeEntity,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val description: String,
    @Column(name = "order_index", nullable = false)
    val orderIndex: Int,
    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    val pages: Set<LessonPageEntity> = LinkedHashSet(),
) {
    override fun equals(other: Any?): Boolean = other is LessonEntity && id == other.id
    override fun hashCode(): Int = id ?: 0
}