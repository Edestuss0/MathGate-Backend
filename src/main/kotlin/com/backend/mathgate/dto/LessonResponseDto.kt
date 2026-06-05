package com.backend.mathgate.dto

data class LessonResponseDto(
    val id: Int,
    val themeId: Int,
    val title: String,
    val description: String,
    val pages: List<LessonPageResponseDto>
)
