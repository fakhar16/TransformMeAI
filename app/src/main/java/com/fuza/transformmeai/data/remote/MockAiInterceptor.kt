package com.fuza.transformmeai.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Returns deterministic placeholder URLs so Retrofit + OkHttp run without a real backend.
 */
class MockAiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val host = request.url.host
        val path = request.url.encodedPath
        val isMockGenerate = host == "api.transformmeai.mock" && path.contains("generate-looks")
        if (!isMockGenerate) {
            return chain.proceed(request)
        }

        val json =
            """
            {
              "images": [
                "https://picsum.photos/seed/transformme1/720/720",
                "https://picsum.photos/seed/transformme2/720/720",
                "https://picsum.photos/seed/transformme3/720/720",
                "https://picsum.photos/seed/transformme4/720/720",
                "https://picsum.photos/seed/transformme5/720/720"
              ]
            }
            """.trimIndent()

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(json.toResponseBody(JSON))
            .build()
    }

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaType()
    }
}