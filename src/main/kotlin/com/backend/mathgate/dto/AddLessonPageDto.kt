package com.backend.mathgate.dto

data class AddLessonPageDto(
    val orderIndex: Int,
    val blocks: List<AddLessonBlockDto>,
)