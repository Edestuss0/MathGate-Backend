package com.backend.mathgate.dto

import jakarta.validation.constraints.Min

data class AddLessonPageDto(
    @field:Min(0) val lessonId: Int,
    @field:Min(0) val orderIndex: Int,
)