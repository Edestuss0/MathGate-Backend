package com.backend.mathgate.dto

data class QuestionDto(
    val id: Int,
    val answer: String,
    val blocks: List<BlockDto>
)