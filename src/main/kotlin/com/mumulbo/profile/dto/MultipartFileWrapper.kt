package com.mumulbo.profile.dto

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import org.springframework.web.multipart.MultipartFile

class MultipartFileWrapper(
    private val content: ByteArray,
    private val originalFilename: String,
    private val contentType: String
) : MultipartFile {
    override fun getName(): String = originalFilename

    override fun getOriginalFilename(): String = originalFilename

    override fun getContentType(): String = contentType

    override fun isEmpty(): Boolean = content.isEmpty()

    override fun getSize(): Long = content.size.toLong()

    override fun getBytes(): ByteArray = content

    override fun getInputStream(): InputStream = ByteArrayInputStream(content)

    override fun transferTo(dest: File) {
        FileOutputStream(dest).use { it.write(content) }
    }
}
