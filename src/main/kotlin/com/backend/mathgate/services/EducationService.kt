package com.backend.mathgate.services

import com.backend.mathgate.dto.AddLessonDto
import com.backend.mathgate.dto.AddThemeDto
import com.backend.mathgate.dto.LessonBlockResponseDto
import com.backend.mathgate.dto.LessonPageResponseDto
import com.backend.mathgate.dto.LessonResponseDto
import com.backend.mathgate.dto.LessonsByPageResponseDto
import com.backend.mathgate.dto.PostResponse
import com.backend.mathgate.entities.LessonBlockEntity
import com.backend.mathgate.entities.LessonEntity
import com.backend.mathgate.entities.LessonPageEntity
import com.backend.mathgate.entities.ThemeEntity
import com.backend.mathgate.repositories.LessonBlocksRepository
import com.backend.mathgate.repositories.LessonPagesRepository
import com.backend.mathgate.repositories.LessonsRepository
import com.backend.mathgate.repositories.ThemeRepository
import com.backend.mathgate.validators.BlockPayloadValidator
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class EducationService(
    private val themeRepository: ThemeRepository,
    private val lessonsRepository: LessonsRepository,
    private val lessonPagesRepository: LessonPagesRepository,
    private val lessonBlocksRepository: LessonBlocksRepository,
    private val blockPayloadValidator: BlockPayloadValidator,
    @Value("\${MODE}") private val appMode: String
) {
    @Transactional
    fun getAllThemes(): List<ThemeEntity> {
        try {
            val themes = themeRepository.findAll()
            return themes
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти темы")
        }
    }

    @Transactional
    fun getThemesByGrade(grade: Int): List<ThemeEntity> {
        try {
            val themes = themeRepository.getByGrade(grade)
            return themes
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти темы для ${grade} класса")
        }
    }

    @Transactional
    fun addTheme(dto: AddThemeDto): PostResponse {
        if (appMode == "release") {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        try {
            themeRepository.save(
                ThemeEntity(
                    name = dto.name,
                    grade = dto.grade,
                )
            )
            return PostResponse("Тема успешно добавлена")
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось добавить тему")
        }
    }

    @Transactional
    fun deleteThemeById(id: Int): PostResponse {
        if (appMode == "release") {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        themeRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти тему")
        }
        try {
            themeRepository.deleteById(id)
            return PostResponse("Тема успешно удалена")
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось удалить тему")
        }
    }

    @Transactional
    fun getLessonByTheme(id: Int): List<LessonsByPageResponseDto> {
        val lessons = lessonsRepository.getAllByThemeId(id)
        if (lessons.isEmpty()) {
            return emptyList()
        }
        return lessons.map { lesson ->
            LessonsByPageResponseDto(
                title = lesson.name,
                description = lesson.description,
                orderIndex = lesson.orderIndex,
                id = lesson.id!!,
            )
        }
    }

    @Transactional
    fun getLessonById(id: Int): LessonResponseDto {
        val lesson = lessonsRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Урок не найден")
        }

        val pages = lessonPagesRepository.getAllByLessonId(lesson.id!!).map { page ->
            val blocks = lessonBlocksRepository.getAllByPageId(page.id!!).map { block ->
                LessonBlockResponseDto(
                    id = block.id!!,
                    blockType = block.blockType,
                    orderIndex = block.orderIndex,
                    payload = block.payload,
                )
            }

            LessonPageResponseDto(
                id = page.id!!,
                blocks = blocks,
                orderIndex = page.orderIndex,
            )
        }

        return LessonResponseDto(
            id = lesson.id!!,
            title = lesson.name,
            description = lesson.description,
            pages = pages,
            themeId = lesson.theme.id!!,
        )
    }

    @Transactional
    fun addLesson(dto: AddLessonDto): PostResponse {
        if (appMode == "release") {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        val theme = themeRepository.findById(dto.theme).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Тема не найдена")
        }

        val lesson = lessonsRepository.save(
            LessonEntity(
                name = dto.name,
                description = dto.description,
                theme = theme,
                orderIndex = dto.orderIndex,
            )
        )

        dto.pages.forEach { pageDto ->
            val page = lessonPagesRepository.save(
                LessonPageEntity(
                    lesson = lesson,
                    orderIndex = pageDto.orderIndex,
                )
            )
            pageDto.blocks.forEach { block ->

                val validatedPayload = blockPayloadValidator.validate(block.payload, block.blockType)

                lessonBlocksRepository.save(
                    LessonBlockEntity(
                        page = page,
                        blockType = block.blockType,
                        orderIndex = block.orderIndex,
                        payload = validatedPayload,
                    )
                )
            }
        }
        return PostResponse("Урок успешно добавлен")
    }
}