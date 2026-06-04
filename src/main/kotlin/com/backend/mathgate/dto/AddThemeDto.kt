package com.backend.mathgate.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AddThemeDto(
    @field:NotBlank val name: String,
    @field:Min(1) val grade: Int,
)