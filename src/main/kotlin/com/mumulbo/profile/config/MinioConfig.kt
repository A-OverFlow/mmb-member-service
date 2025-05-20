package com.mumulbo.profile.config

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig(
    @Value("\${minio.host}")
    private val host: String,
    @Value("\${minio.port}")
    private val port: String,
    @Value("\${minio.access-key}")
    private val accessKey: String,
    @Value("\${minio.secret-key}")
    private val secretKey: String
) {
    @Bean
    fun minIO(): MinioClient = MinioClient.builder()
        .endpoint("$host:$port")
        .credentials(accessKey, secretKey)
        .build();
}
