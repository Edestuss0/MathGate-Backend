package com.backend.mathgate.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class UpdateLessonDto(
    @field:NotBlank val name: String,
    @field:NotBlank val description: String,
    @field:Min(0) val orderIndex: Int,
)