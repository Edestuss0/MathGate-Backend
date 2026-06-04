package com.backend.mathgate

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment

@SpringBootApplication
class MathgateApplication {}

fun main(args: Array<String>) {
    runApplication<MathgateApplication>(*args)
}