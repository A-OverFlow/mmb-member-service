package com.mumulbo.member

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<MmbMemberServiceApplication>().with(TestcontainersConfiguration::class).run(*args)
}
