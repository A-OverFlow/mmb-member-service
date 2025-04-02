package com.mumulbo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MmbMemberServiceApplication

fun main(args: Array<String>) {
    runApplication<MmbMemberServiceApplication>(*args)
}
