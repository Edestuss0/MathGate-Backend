package com.backend.mathgate.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "questions")
data class QuestionEntity(
    @Id
    val id: Int,

    @Column(nullable = false)
    val answer: String,
)