package org.bin.demo

sealed class ApiError(override val message: String) : Throwable(message) {

    data class NetworkError(
        override val message: String = "서버와 통신이 원활하지 않습니다. 접속 정보를 확인해주세요",
        override val cause: Throwable? = null
    ) : ApiError(message)

    data class ApiResultError(
        val code: String? = null,
        val apiMessage: String = "요청 처리 중 오류가 발생했습니다."
    ) : ApiError(apiMessage) {
        override val message: String = apiMessage
    }

    data class HttpError(
        val code: Int,
        val httpMessage: String = "HTTP Request Fail",
        val errorBody: String? = null
    ) : ApiError("HTTP Error $code: $httpMessage")


    data class UnknownError(
        override val message: String = "UNKNOWN Error",
        override val cause: Throwable? = null
    ) : ApiError(message)
}