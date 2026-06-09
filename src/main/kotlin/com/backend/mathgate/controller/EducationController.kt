package com.backend.mathgate.controller

import com.backend.mathgate.dto.AddLessonBlockDto
import com.backend.mathgate.dto.AddLessonDto
import com.backend.mathgate.dto.AddLessonPageDto
import com.backend.mathgate.dto.AddThemeDto
import com.backend.mathgate.dto.LessonResponseDto
import com.backend.mathgate.dto.LessonsByPageResponseDto
import com.backend.mathgate.dto.PostResponse
import com.backend.mathgate.dto.ThemeFullResponseDto
import com.backend.mathgate.dto.ThemeResponseDto
import com.backend.mathgate.dto.UpdateLessonBlockDto
import com.backend.mathgate.dto.UpdateLessonDto
import com.backend.mathgate.services.EducationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/education")
class EducationController(
    private val educationService: EducationService
) {
    @GetMapping("themes")
    fun getAllThemes(): List<ThemeResponseDto> {
        return educationService.getAllThemes()
    }

    @GetMapping("themes/grade/{grade}")
    fun getByGrade(@PathVariable grade: Int): List<ThemeResponseDto> {
        return educationService.getThemesByGrade(grade)
    }

    @GetMapping("themes/grade/{grade}/download")
    fun downloadThemesByGrade(@PathVariable grade: Int): List<ThemeFullResponseDto> {
        return educationService.getFullThemesByGrade(grade)
    }

    @PostMapping("theme/add")
    fun addTheme(@Valid @RequestBody dto: AddThemeDto): PostResponse {
        return educationService.addTheme(dto)
    }

    @PutMapping("theme/{id}")
    fun updateTheme(@PathVariable id: Int, @Valid @RequestBody dto: AddThemeDto): PostResponse {
        return educationService.updateTheme(dto, id)
    }

    @DeleteMapping("theme/delete/{id}")
    fun deleteTheme(@PathVariable id: Int): PostResponse {
        return educationService.deleteThemeById(id)
    }

    @GetMapping("theme/{id}/lessons")
    fun getLessonsByTheme(@PathVariable id: Int): List<LessonsByPageResponseDto> {
        return educationService.getLessonByTheme(id)
    }

    @GetMapping("lesson/{id}")
    fun getLessonById(@PathVariable id: Int): LessonResponseDto {
        return educationService.getLessonById(id)
    }

    @PutMapping("lesson/{id}")
    fun updateLesson(@PathVariable id: Int, @RequestBody dto: UpdateLessonDto): PostResponse {
        return educationService.updateLesson(dto, id)
    }

    @DeleteMapping("lesson/delete/{id}")
    fun deleteLessonById(@PathVariable id: Int): PostResponse {
        return educationService.deleteLessonById(id)
    }

    @PostMapping("lesson/add")
    fun addLesson(@Valid @RequestBody dto: AddLessonDto): PostResponse {
        return educationService.addLesson(dto)
    }

    @PostMapping("page/add")
    fun addPage(@Valid @RequestBody dto: AddLessonPageDto): PostResponse {
        return educationService.addLessonPage(dto)
    }

    @DeleteMapping("page/delete/{id}")
    fun deletePage(@PathVariable id: Int): PostResponse {
        return educationService.deletePageById(id)
    }

    @PostMapping("block/add")
    fun addBlock(@Valid @RequestBody dto: AddLessonBlockDto): PostResponse {
        return educationService.addLessonBlock(dto)
    }

    @PutMapping("block/{id}")
    fun updateBlock(@PathVariable id: Int, @Valid @RequestBody dto: UpdateLessonBlockDto): PostResponse {
        return educationService.updateBlock(dto, id)
    }

    @DeleteMapping("block/delete/{id}")
    fun deleteBlock(@PathVariable id: Int): PostResponse {
        return educationService.deleteLessonBlockById(id)
    }

}