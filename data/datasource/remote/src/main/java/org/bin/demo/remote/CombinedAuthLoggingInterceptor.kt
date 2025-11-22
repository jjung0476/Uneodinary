package org.bin.demo.remote


import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.bin.demo.debug
import org.bin.demo.token.TokenManager
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CombinedAuthLoggingInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request() // 원본 요청
        val requestBuilder = originalRequest.newBuilder()

        val newBaseUrl = ""
        debug("newBaseUrl : $newBaseUrl")

        val httpUrl = newBaseUrl.toHttpUrlOrNull()
            ?: return chain.proceed(originalRequest)

        val newUrl = originalRequest.url
            .newBuilder()
            .scheme(httpUrl.scheme)
            .host(httpUrl.host)
            .port(httpUrl.port)
            .build()

        val skipAuthPaths = setOf(
            "/api/v1/auth/",
            "/api/v1/app/logs/",
        )

        val path = originalRequest.url.encodedPath
        val shouldTokenSkip = skipAuthPaths.any { path == it || path.startsWith("$it/") }

        //  skipAuthPaths API 호출 시에는 토큰을 추가하지 않음
        if (!shouldTokenSkip) {
            val token = runBlocking {
                tokenManager.getAccessToken().first()
            }
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.url(newUrl).build()
        logRequest(request)
        val response = chain.proceed(request) // 네트워크 요청 실행

        return logResponse(response) // 응답 로깅 후 반환
    }

    private fun logRequest(request: Request) {
        debug("OkHttp", "--> ${request.method} ${request.url}")
        for (header in request.headers) {
            debug("OkHttp", "header ${header.first}: ${header.second}")
        }

        val requestBody = request.body
        if (requestBody != null) {
            val contentType = requestBody.contentType()
            if (contentType != null && isImageOrBinary(contentType.toString())) {
                debug("OkHttp", "    Request Body: (Binary/Image content, skipped)")
            } else {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                debug("OkHttp", "    Request Body: ${buffer.readString(Charset.forName("UTF-8"))}")
            }
        }
        debug("OkHttp", "--> END ${request.method}")
    }

    private fun logResponse(response: Response): Response {
        val request: Request = response.request
        debug("<-- ${response.code} ${response.message} ${request.url}")

        for (header in response.headers) {
            debug("    ${header.first}: ${header.second}")
        }

        val responseBody = response.body
        if (responseBody == null) {
            debug("    Response Body: (No content)")
            return response // 본문이 없으면 원본 응답을 그대로 반환
        }

        // 응답 본문의 Content-Type을 확인하여 이미지/바이너리 여부 판단
        val contentType = responseBody.contentType()
        if (contentType != null && isImageOrBinary(contentType.toString())) {
            debug("    Response Body: (Binary/Image content, skipped)")
            return response // 바이너리/이미지인 경우 원본 응답을 그대로 반환 (본문을 읽지 않음)
        }

        var responseBodyString: String? = null
        try {
            // 원본 ResponseBody의 source()를 사용해서 본문을 완전히 읽어 Buffer에 저장
            // 이 시점에서 원본 ResponseBody는 소비
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // 모든 데이터를 버퍼로 로드
            val buffer = source.buffer

            // Charset을 가져오거나 기본으로 UTF-8 사용
            val charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

            // 주의: buffer.clone()을 사용하면 원본 buffer는 유지되지만,
            // 이 방식은 원본 ResponseBody가 소비되므로,
            // 새로운 ResponseBody를 생성하여 반환
            responseBodyString = buffer.clone().readString(charset)
            debug("    Response Body: $responseBodyString")

        } catch (e: Exception) {
            Log.e("OkHttpLog", "Failed to read response body for logging: ${e.message}", e)
            responseBodyString = "(Failed to read body: ${e.message})"
            debug("    Response Body: $responseBodyString")
        }

        // 원본 responseBody가 이미 소비되었으므로,
        // 다음 인터셉터나 최종 사용자에게 전달될 새로운 ResponseBody를 생성하여 반환
        val newResponseBody = ResponseBody.create(contentType, responseBodyString ?: "")
        return response.newBuilder()
            .body(newResponseBody)
            .build()
    }


    // Content-Type이 이미지 또는 바이너리 데이터인지 확인하는 헬퍼 함수
    private fun isImageOrBinary(contentType: String): Boolean {
        return contentType.startsWith("image/", ignoreCase = true) ||
                contentType.contains("application/octet-stream", ignoreCase = true) ||
                contentType.startsWith("video/", ignoreCase = true) ||
                contentType.contains("multipart/", ignoreCase = true)
    }

    // AuthInterceptor에 있던 errorResponse 함수
    private fun errorResponse(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_2) // 프로토콜 지정
        .code(401) // 상태 코드
        .message("Authentication required or token missing for this request.") // 메시지
        .body("".toResponseBody(null)) // 빈 바디 (null이 아닌 toResponseBody 사용)
        .build()
}