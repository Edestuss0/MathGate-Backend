package com.backend.mathgate.dto

import jakarta.validation.constraints.NotBlank

data class AddQuestionDto(
    @field:NotBlank
    val id: Int,
    @field:NotBlank
    val answer: String,
    @field:NotBlank
    val blocks: List<BlockDto>,
)