package com.backend.mathgate.dto

import tools.jackson.databind.JsonNode

data class LessonBlockResponseDto(
    val id: Int,
    val blockType: LessonBlockType,
    val orderIndex: Int,
    val payload: JsonNode,
)
