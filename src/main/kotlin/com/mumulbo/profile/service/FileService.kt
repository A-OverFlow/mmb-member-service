package com.mumulbo.profile.service

import com.mumulbo.profile.dto.MultipartFileWrapper
import com.mumulbo.profile.exception.InvalidFileException
import io.minio.MinioClient
import io.minio.PutObjectArgs
import java.io.ByteArrayOutputStream
import java.net.URL
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ulid.ULID

@Service
class FileService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket}")
    private val bucket: String
) {
    private val tika = Tika()

    fun urlToMultipartFile(url: URL): MultipartFile {
        val connection = url.openConnection()

        val inputStream = connection.getInputStream()
        val outputStream = ByteArrayOutputStream()

        val buffer = ByteArray(8192)
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        val content = outputStream.toByteArray()
        val fileName = ULID.nextULID().toString()
        val contentType = connection.contentType
        return MultipartFileWrapper(content, fileName, contentType)
    }

    fun uploadImage(file: MultipartFile): String {
        if (!tika.detect(file.inputStream).startsWith("image/")) {
            throw InvalidFileException()
        }

        val objectName = "profiles/${ULID.nextULID()}"
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .`object`(objectName)
                .stream(file.inputStream, file.size, -1)
                .contentType(file.contentType)
                .build()
        )

        return "$bucket/$objectName"
    }
}
