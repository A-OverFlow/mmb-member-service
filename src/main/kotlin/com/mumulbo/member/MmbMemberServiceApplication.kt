package com.mumulbo.member

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MmbMemberServiceApplication

fun main(args: Array<String>) {
    runApplication<MmbMemberServiceApplication>(*args)
}
