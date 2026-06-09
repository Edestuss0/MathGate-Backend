package com.backend.mathgate.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table

@Entity
@Table(name = "themes")
data class ThemeEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val grade: Int,
    @OneToMany(mappedBy = "theme", fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    val lessons: Set<LessonEntity> = LinkedHashSet(),
) {
    override fun equals(other: Any?): Boolean = other is ThemeEntity && id == other.id
    override fun hashCode(): Int = id ?: 0
}