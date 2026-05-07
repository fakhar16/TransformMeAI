package com.fuza.transformmeai.data.remote

import com.fuza.transformmeai.data.model.LooksResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TransformApiService {
    @Multipart
    @POST("v1/generate-looks")
    suspend fun generateLooks(
        @Part image: MultipartBody.Part,
    ): LooksResponseDto
}