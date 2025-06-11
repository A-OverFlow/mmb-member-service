package com.mumulbo.common.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Component
@Aspect
class LoggingAspect {
    private val log = KotlinLogging.logger { }

    @Around(
        "execution(* *(..)) && (" +
            "@within(org.springframework.web.bind.annotation.RestController) || " +
            "@within(org.springframework.stereotype.Service) || " +
            "@within(org.springframework.stereotype.Repository))"
    )
    fun logMethodExecution(joinPoint: ProceedingJoinPoint): Any? {
        val className = String.format("%-30s", joinPoint.signature.declaringType.simpleName)
        val methodName = String.format("%-30s", joinPoint.signature.name)
        log.info { "➡️ Entering : $className #$methodName" }

        val startTime = System.currentTimeMillis()
        return try {
            val result = joinPoint.proceed()
            val duration = String.format("%4d", System.currentTimeMillis() - startTime)
            log.info { "✅ Completed: $className #$methodName in    $duration ms" }
            result
        } catch (e: RuntimeException) {
            val duration = String.format("%4d", System.currentTimeMillis() - startTime)
            log.error { "❌ Failed   : $className #$methodName after $duration ms" }
            throw e
        }
    }
}
