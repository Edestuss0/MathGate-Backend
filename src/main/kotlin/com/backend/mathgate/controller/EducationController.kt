package com.backend.mathgate.controller

import com.backend.mathgate.dto.AddLessonDto
import com.backend.mathgate.dto.AddThemeDto
import com.backend.mathgate.dto.PostResponse
import com.backend.mathgate.entities.LessonEntity
import com.backend.mathgate.entities.ThemeEntity
import com.backend.mathgate.services.EducationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/education")
class EducationController(
    private val educationService: EducationService
) {
    @GetMapping("themes")
    fun getAllThemes(): List<ThemeEntity> {
        return educationService.getAllThemes()
    }

    @GetMapping("themes/grade/{grade}")
    fun getByGrade(@PathVariable grade: Int): List<ThemeEntity> {
        return educationService.getThemesByGrade(grade)
    }

    @PostMapping("theme/add")
    fun addTheme(@Valid @RequestBody dto: AddThemeDto): PostResponse {
        return educationService.addTheme(dto)
    }

    @DeleteMapping("theme/delete/{id}")
    fun deleteTheme(@PathVariable id: Int): PostResponse {
        return educationService.deleteThemeById(id)
    }

    @GetMapping("lessons/{id}")
    fun getLesson(@PathVariable id: Int): List<LessonEntity> {
        return educationService.getLessonByTheme(id)
    }

    @PostMapping("lesson/add")
    fun addLesson(@Valid @RequestBody dto: AddLessonDto): PostResponse {
        return educationService.addLesson(dto)
    }

}