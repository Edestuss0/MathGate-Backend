package com.backend.mathgate.dto

data class LessonPageResponseDto(
    val id: Int,
    val orderIndex: Int,
    val blocks: List<LessonBlockResponseDto>
)
