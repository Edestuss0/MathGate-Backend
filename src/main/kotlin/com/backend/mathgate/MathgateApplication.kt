package com.backend.mathgate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class MathgateApplication{}

fun main(args: Array<String>) {
    runApplication<MathgateApplication>(*args)
}