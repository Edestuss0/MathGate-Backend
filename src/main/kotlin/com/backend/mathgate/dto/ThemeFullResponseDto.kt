package com.backend.mathgate.dto

data class ThemeFullResponseDto(
    val id: Int,
    val name: String,
    val grade: Int,
    val lessons: List<LessonResponseDto>
)
