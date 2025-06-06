package com.mumulbo.common.config

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class ApiLoggingFilter : OncePerRequestFilter() {
    private val log = KotlinLogging.logger { }
    private val allowedHeaders = setOf("x-user-id", "content-type", "content-length")

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        filterChain.doFilter(wrappedRequest, wrappedResponse)
        logRequestDetails(wrappedRequest)
        logResponseDetails(wrappedResponse)

        wrappedResponse.copyBodyToResponse()
    }

    private fun logRequestDetails(request: ContentCachingRequestWrapper) {
        val requestLog = StringBuilder("➡️ [ Request Detail ]\n")
        // 1. 요청 URL
        var url = request.requestURI
        val queryString = request.queryString
        if (queryString != null) {
            url += "?$queryString"
        }
        requestLog.append("\tURL:\n")
        requestLog.append("\t\t[${request.method}] $url\n")

        // 2. Headers
        requestLog.append("\tHeaders:\n")
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val name = headerNames.nextElement()
            if (name in allowedHeaders) {
                val value = request.getHeader(name)
                requestLog.append("\t\t$name: $value\n")
            }
        }

        // 3. Body
        val contentType = request.contentType
        if (contentType != null && (contentType.contains("application/json") || contentType.contains("application/x-www-form-urlencoded"))) {
            val content = request.contentAsByteArray
            if (content.isNotEmpty()) {
                val body = String(content, StandardCharsets.UTF_8)
                requestLog.append("\tRequest Body:\n")
                requestLog.append("\t\t$body\n")
            }
        }

        // 4. Parts (multipart/form-data)
        if (contentType != null && contentType.contains("multipart/form-data")) {
            try {
                requestLog.append("\tMultipart Parts:\n")
                for (part in request.parts) {
                    requestLog.append("\t\t${part.name} [size: ${part.size} bytes]\n")
                }
            } catch (e: Exception) {
                requestLog.append("❌ Multipart parsing error: ${e.message}\n")
            }
        }
        log.info { "\n$requestLog" }
    }

    private fun logResponseDetails(response: ContentCachingResponseWrapper) {
        val responseLog = if (HttpStatusCode.valueOf(response.status).is2xxSuccessful) {
            StringBuilder("✅ [ Response Detail ]\n")
        } else {
            StringBuilder("❌ [ Response Detail ]\n")
        }
        responseLog.append("\tResponse Status:\n")
        responseLog.append("\t\t${HttpStatus.valueOf(response.status).reasonPhrase}\n")
        val content = response.contentAsByteArray
        if (content.isNotEmpty()) {
            val body = String(content, StandardCharsets.UTF_8)
            responseLog.append("\tResponse Body:\n")
            responseLog.append("\t\t$body\n")
        }

        if (HttpStatusCode.valueOf(response.status).is2xxSuccessful) {
            log.info { "\n$responseLog" }
        } else {
            log.error { "\n$responseLog" }
        }
    }
}
