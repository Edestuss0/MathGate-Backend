package com.backend.mathgate.dto

data class LessonsByPageResponseDto(
    val title: String,
    val description: String,
    val orderIndex: Int,
    val id: Int
)
