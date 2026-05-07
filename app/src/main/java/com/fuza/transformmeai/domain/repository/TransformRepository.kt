package com.fuza.transformmeai.domain.repository

import java.io.File

interface TransformRepository {
    suspend fun generateLooks(image: File): Result<List<String>>

    suspend fun downloadRemoteImageToPictures(url: String): Result<String>
}