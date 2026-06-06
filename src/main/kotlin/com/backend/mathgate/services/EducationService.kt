package com.backend.mathgate.services

import com.backend.mathgate.dto.AddLessonBlockDto
import com.backend.mathgate.dto.AddLessonDto
import com.backend.mathgate.dto.AddLessonPageDto
import com.backend.mathgate.dto.AddThemeDto
import com.backend.mathgate.dto.LessonBlockResponseDto
import com.backend.mathgate.dto.LessonPageResponseDto
import com.backend.mathgate.dto.LessonResponseDto
import com.backend.mathgate.dto.LessonsByPageResponseDto
import com.backend.mathgate.dto.PostResponse
import com.backend.mathgate.dto.ThemeResponseDto
import com.backend.mathgate.dto.UpdateLessonBlockDto
import com.backend.mathgate.dto.UpdateLessonDto
import com.backend.mathgate.entities.LessonBlockEntity
import com.backend.mathgate.entities.LessonEntity
import com.backend.mathgate.entities.LessonPageEntity
import com.backend.mathgate.entities.ThemeEntity
import com.backend.mathgate.repositories.LessonBlocksRepository
import com.backend.mathgate.repositories.LessonPagesRepository
import com.backend.mathgate.repositories.LessonsRepository
import com.backend.mathgate.repositories.ThemeRepository
import com.backend.mathgate.validators.BlockPayloadValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper

@Service
class EducationService(
    private val themeRepository: ThemeRepository,
    private val lessonsRepository: LessonsRepository,
    private val lessonPagesRepository: LessonPagesRepository,
    private val lessonBlocksRepository: LessonBlocksRepository,
    private val blockPayloadValidator: BlockPayloadValidator,
    private val objectMapper: ObjectMapper,
    @Value("\${IS_PRODUCTION:false}") private val isProduction: Boolean
) {
    @Transactional(readOnly = true)
    @Cacheable(value = ["themes"])
    fun getAllThemes(): List<ThemeResponseDto> {
        try {
            val themes = themeRepository.findAll().map { theme ->
                ThemeResponseDto(id = theme.id!!, name = theme.name, grade = theme.grade)
            }
            return themes
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти темы")
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["themes"], key = "#grade")
    fun getThemesByGrade(grade: Int): List<ThemeResponseDto> {
        try {
            val themes = themeRepository.getByGrade(grade).map { theme ->
                ThemeResponseDto(id = theme.id!!, name = theme.name, grade = theme.grade)
            }
            return themes
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти темы для ${grade} класса")
        }
    }

    @Transactional
    @CacheEvict(value = ["themes"], allEntries = true)
    fun addTheme(dto: AddThemeDto): PostResponse {
        if (isProduction) {
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
    @Caching(evict = [
        CacheEvict(value = ["themes"], allEntries = true),
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun deleteThemeById(id: Int): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        themeRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти тему")
        }
        themeRepository.deleteById(id)
        return PostResponse("Тема успешно удалена")
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun deleteLessonById(id: Int): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        lessonsRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти урок")
        }
        lessonsRepository.deleteById(id)
        return PostResponse("Урок успешно удалён")
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun deletePageById(id: Int): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        lessonPagesRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти страницу")
        }
        lessonPagesRepository.deleteById(id)
        return PostResponse("Страница успешно удалена")
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun deleteLessonBlockById(id: Int): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        lessonBlocksRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти блок страницы")
        }
        lessonBlocksRepository.deleteById(id)
        return PostResponse("Блок страницы успешно удалён")
    }

    @Transactional
    @CacheEvict(value = ["themes"], key = "#id")
    fun updateTheme(dto: AddThemeDto, id: Int): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        val theme = themeRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти тему")
        }
        try {
            themeRepository.save(theme.copy(name = dto.name, grade = dto.grade))
            return PostResponse("Тема успешно изменена")
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось изменить тему")
        }
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun updateLesson(dto: UpdateLessonDto, id: Int): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        val lesson = lessonsRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти урок")
        }
        try {
            lessonsRepository.save(lesson.copy(name = dto.name, orderIndex = dto.orderIndex, description = dto.description))
            return PostResponse("Урок успешно изменён")
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось изменить урок")
        }
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun updateBlock(dto: UpdateLessonBlockDto, id: Int): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        val block = lessonBlocksRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти блок страницы")
        }
        try {
            val payload = blockPayloadValidator.validate(dto.payload, dto.blockType)
            lessonBlocksRepository.save(block.copy(blockType = dto.blockType, orderIndex = dto.orderIndex, payload = payload))
            return PostResponse("Блок страницы успешно изменён")
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось изменить блок страницы")
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["lessonsByTheme"], key = "#id")
    fun getLessonByTheme(id: Int): List<LessonsByPageResponseDto> {
        val lessons = lessonsRepository.findAllByThemeId(id)
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

    @Transactional(readOnly = true)
    @Cacheable(value = ["lessons"], key = "#id")
    fun getLessonById(id: Int): LessonResponseDto {

        val lesson = lessonsRepository.findByIdWithPagesAndBlocks(id) ?:
           throw ResponseStatusException(HttpStatus.NOT_FOUND, "Урок не найден")

        val pageResponses = lesson.pages.map { page ->
            val blocks = page.blocks.map { block ->
                LessonBlockResponseDto(
                    id = block.id!!,
                    blockType = block.blockType,
                    orderIndex = block.orderIndex,
                    payload = objectMapper.readTree(block.payload)
                )
            }
            LessonPageResponseDto(id = page.id!!, blocks = blocks, orderIndex = page.orderIndex)
        }

        return LessonResponseDto(
            id = lesson.id!!,
            title = lesson.name,
            description = lesson.description,
            pages = pageResponses,
            themeId = lesson.theme.id!!,
        )
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun addLesson(dto: AddLessonDto): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        val theme = themeRepository.findById(dto.theme).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Тема не найдена")
        }

       lessonsRepository.save(
            LessonEntity(
                name = dto.name,
                description = dto.description,
                theme = theme,
                orderIndex = dto.orderIndex,
            )
        )
        return PostResponse("Урок успешно добавлен")
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun addLessonPage(dto: AddLessonPageDto): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        val lesson = lessonsRepository.findById(dto.lessonId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Урок не найден")
        }

        lessonPagesRepository.save(
            LessonPageEntity(
                lesson = lesson,
                orderIndex = dto.orderIndex,
            )
        )
        return PostResponse("Страница успешно добавлена")
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["lessons"], allEntries = true),
        CacheEvict(value = ["lessonsByTheme"], allEntries = true)
    ])
    fun addLessonBlock(dto: AddLessonBlockDto): PostResponse {
        if (isProduction) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ к этой функции запрещён")
        }
        val page = lessonPagesRepository.findById(dto.lessonPageId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Страница не найдена")
        }

        val payload = blockPayloadValidator.validate(dto.payload, dto.blockType)

        lessonBlocksRepository.save(
            LessonBlockEntity(
                page = page,
                orderIndex = dto.orderIndex,
                blockType = dto.blockType,
                payload = payload,
            )
        )

        return PostResponse("Блок страницы успешно добавлен")
    }
}