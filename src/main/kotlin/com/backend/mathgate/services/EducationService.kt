package com.backend.mathgate.services

import com.backend.mathgate.dto.AddLessonDto
import com.backend.mathgate.dto.AddThemeDto
import com.backend.mathgate.dto.PostResponse
import com.backend.mathgate.entities.LessonEntity
import com.backend.mathgate.entities.ThemeEntity
import com.backend.mathgate.repositories.LessonsRepository
import com.backend.mathgate.repositories.ThemeRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class EducationService(
    private val themeRepository: ThemeRepository,
    private val lessonsRepository: LessonsRepository
) {
    @Transactional
    fun getAllThemes(): List<ThemeEntity> {
        try {
            val themes = themeRepository.findAll()
            return themes
        } catch (e: Exception) {
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
    fun getLessonByTheme(id: Int): List<LessonEntity> {
        val lessons = lessonsRepository.getAllByThemeId(id)
        if (lessons.isEmpty()) {
            return emptyList()
        }
        return lessons
    }

    @Transactional
    fun addLesson(dto: AddLessonDto): PostResponse {
        try {
            lessonsRepository.save(
                LessonEntity(
                    name = dto.name,
                    description = dto.description,
                    theme = dto.theme,
                )
            )
            return PostResponse("Урок успешно добавлен")
        } catch (e: Exception) {
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}