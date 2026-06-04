package com.backend.mathgate.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AddLessonDto(
    @field:Min(0) val theme: Int,
    @field:NotBlank val name: String,
    @field:NotBlank val description: String,
)