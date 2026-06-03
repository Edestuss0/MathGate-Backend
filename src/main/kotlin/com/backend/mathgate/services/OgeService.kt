package com.backend.mathgate.services

import com.backend.mathgate.dto.AddQuestionDto
import com.backend.mathgate.dto.BlockDto
import com.backend.mathgate.dto.QuestionDto
import com.backend.mathgate.entities.BlockEntity
import com.backend.mathgate.entities.QuestionEntity
import com.backend.mathgate.repositories.BlockRepository
import com.backend.mathgate.repositories.QuestionRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class OgeService(
    private val questionRepository: QuestionRepository,
    private val blockRepository: BlockRepository,
) {

    @Transactional
    fun getQuestion(): QuestionDto {
        val randomQuestion = questionRepository.findRandomQuestion()

        if (randomQuestion == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Вопросы не найдены")
        }

        val blocksFinal = blockRepository
            .findAllByQuestion(randomQuestion.id)
            .map {
                BlockDto(it.type, it.content)
            }

        return QuestionDto(
            id = randomQuestion.id,
            answer = randomQuestion.answer,
            blocks = blocksFinal,
        )
    }
    @Transactional
    fun create(dto: AddQuestionDto): String {
        try {
            questionRepository.save(
                QuestionEntity(
                    id = dto.id,
                    answer = dto.answer,
                )
            )
            val blocks = dto.blocks

            blocks.forEach { block ->
                blockRepository.save(
                    BlockEntity(
                        question = dto.id,
                        type = block.type,
                        content = block.content,
                    )
                )
            }

            return "Success"

        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}