package com.backend.mathgate.controller

import com.backend.mathgate.dto.AddQuestionDto
import com.backend.mathgate.dto.QuestionDto
import com.backend.mathgate.services.OgeService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/oge")
class OgeController(
    private val service: OgeService
) {
    @GetMapping
    fun getOge(): QuestionDto? {
        return service.getQuestion()
    }

    @PostMapping("add")
    fun addQuestion(@Valid @RequestBody question: AddQuestionDto): String {
        return service.create(question)
    }
}