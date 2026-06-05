package com.backend.mathgate.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import tools.jackson.databind.JsonNode

data class AddLessonBlockDto(
    @field:NotNull
    val blockType: LessonBlockType,
    @field:Min(1)
    val orderIndex: Int,
    @field:NotNull
    val payload: JsonNode,
)