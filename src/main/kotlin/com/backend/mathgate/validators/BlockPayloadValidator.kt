package com.backend.mathgate.validators

import com.backend.mathgate.dto.ChoiceQuestionPayloadType
import com.backend.mathgate.dto.FormulaPayloadType
import com.backend.mathgate.dto.InputQuestionPayloadType
import com.backend.mathgate.dto.LessonBlockType
import com.backend.mathgate.dto.SvgPayloadType
import com.backend.mathgate.dto.TextPayloadType
import jakarta.validation.Validator
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper

@Service
class BlockPayloadValidator(
    private val objectMapper: ObjectMapper,
    private val validator: Validator
) {
    fun validate(payload: JsonNode, blockType: LessonBlockType): String {
        val dto = parsePayload(blockType, payload)

        validator.validate(dto)

        validateBusinessRules(dto, blockType)

        return objectMapper.writeValueAsString(dto)
    }

    private fun parsePayload(
        blockType: LessonBlockType,
        payload: JsonNode
    ): Any {
        return when (blockType) {
            LessonBlockType.TEXT ->
                objectMapper.treeToValue(
                    payload,
                    TextPayloadType::class.java
                )

            LessonBlockType.INPUT_QUESTION ->
                objectMapper.treeToValue(
                    payload,
                    InputQuestionPayloadType::class.java
                )

            LessonBlockType.CHOICE_QUESTION ->
                objectMapper.treeToValue(
                    payload,
                    ChoiceQuestionPayloadType::class.java
                )

            LessonBlockType.SVG ->
                objectMapper.treeToValue(
                    payload,
                    SvgPayloadType::class.java
                )

            LessonBlockType.FORMULA ->
                objectMapper.treeToValue(
                    payload,
                    FormulaPayloadType::class.java
                )
        }
    }

    private fun validateAnnotations(
        dto: Any
    ) {
        val violations = validator.validate(dto)

        if (violations.isNotEmpty()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,

                violations.joinToString(separator = "\n") {
                    "${it.propertyPath}: ${it.message}"
                },
            )
        }
    }

    private fun validateBusinessRules(dto: Any, blockType: LessonBlockType) {
        when (blockType) {
            LessonBlockType.CHOICE_QUESTION -> {
                val choiceDto = dto as ChoiceQuestionPayloadType
                validator.validate(choiceDto)
                validateChoiceQuestion(choiceDto)
            }

            LessonBlockType.SVG -> {
                val svgDto = dto as SvgPayloadType
                validateSvg(svgDto)
            }

            else -> {}
        }
    }

    private fun validateSvg(
        dto: SvgPayloadType
    ) {

        val svg = dto.svg.trim()

        if (!svg.startsWith("<svg")) {

            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Некорректный SVG"
            )
        }

        if (!svg.contains("</svg>")) {

            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "SVG не закрыт"
            )
        }
    }

    private fun validateChoiceQuestion(
        dto: ChoiceQuestionPayloadType
    ) {

        dto.answers.forEach {

            if (it.isBlank()) {

                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Пустой вариант ответа"
                )
            }
        }
    }
}