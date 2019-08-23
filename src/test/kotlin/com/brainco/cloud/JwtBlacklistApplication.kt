package com.brainco.cloud

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JwtBlacklistApplication

fun main(args: Array<String>) {
    runApplication<JwtBlacklistApplication>(*args)
}
