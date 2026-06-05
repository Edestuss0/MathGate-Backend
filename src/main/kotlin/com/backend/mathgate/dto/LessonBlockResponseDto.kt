package com.backend.mathgate.dto

data class LessonBlockResponseDto(
    val id: Int,
    val blockType: LessonBlockType,
    val orderIndex: Int,
    val payload: String,
)
