package com.backend.mathgate.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class TextPayloadType(
    @field:NotBlank
    @field:Size(max = 5000)
    val text: String,
)

data class SvgPayloadType(
    @field:NotBlank
    val svg: String,
)

data class InputQuestionPayloadType(
    @field:NotBlank
    val question: String,
    @field:NotBlank
    val answer: String,
)

data class FormulaPayloadType(
    @field:NotBlank
    val formula: String,
)

data class ChoiceQuestionPayloadType(
    @field:NotBlank
    val question: String,
    @field:NotEmpty @field:Size(min = 2)
    val answers: List<String>,
    @field:Min(0)
    val correctAnswerIndex: Int,
)