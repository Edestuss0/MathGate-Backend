package com.backend.mathgate.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class AddQuestionDto(
    @field:Min(1)
    val id: Int,
    @field:NotBlank()
    val answer: String,
    @field:NotEmpty
    val blocks: List<BlockDto>,
)