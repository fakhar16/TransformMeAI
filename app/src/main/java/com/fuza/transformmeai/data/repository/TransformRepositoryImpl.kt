package com.fuza.transformmeai.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.fuza.transformmeai.data.remote.TransformApiService
import com.fuza.transformmeai.domain.repository.TransformRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransformRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: TransformApiService,
    private val okHttpClient: OkHttpClient,
) : TransformRepository {

    override suspend fun generateLooks(image: File): Result<List<String>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", image.name, body)
                val response = api.generateLooks(part)
                require(response.images.size == 5) { "Expected 5 looks from the API." }
                response.images
            }
        }

    override suspend fun downloadRemoteImageToPictures(url: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder().url(url).build()
                val response = okHttpClient.newCall(request).execute()
                check(response.isSuccessful) { "Download failed (${response.code})" }
                val bytes =
                    response.body?.bytes() ?: error("Empty response body")

                val fileName = "TransformMe_${System.currentTimeMillis()}.jpg"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values =
                        ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TransformMeAI")
                            put(MediaStore.MediaColumns.IS_PENDING, 1)
                        }
                    val resolver = context.contentResolver
                    val uri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                            ?: error("Unable to create MediaStore row")
                    resolver.openOutputStream(uri, "w").use { out ->
                        requireNotNull(out) { "Unable to open output stream" }
                        out.write(bytes)
                    }
                    values.clear()
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                    uri.toString()
                } else {
                    @Suppress("DEPRECATION")
                    val dir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            .resolve("TransformMeAI")
                            .apply { mkdirs() }
                    val file = File(dir, fileName)
                    FileOutputStream(file).use { it.write(bytes) }
                    file.absolutePath
                }
            }
        }
}