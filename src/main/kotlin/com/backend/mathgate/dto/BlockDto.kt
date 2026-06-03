package com.backend.mathgate.dto

import jakarta.validation.constraints.NotBlank

data class BlockDto(
    val type: BlockType,
    @field:NotBlank
    val content: String
)