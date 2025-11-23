package com.example.uneodinary

import android.util.Log
import kotlin.Result

class AuthRepository(private val service: AuthService) {

    //1.로그인
    suspend fun Login(
        req: LoginRequest
    ): Result<List<Report>> = try {
        val response = service.login(req)

        //성공 리턴 시
        if (response.isSuccessful) {
            val body = response.body()
            //body가 없으면
            if (body == null) {
                Log.d("tag", "Response body is null")
                Result.failure(RuntimeException("Response body is null"))
            }
            //data 값이 없을 때
            else if (body.data == null) {
                Log.d("tag", "Response OK but Data is null")
                Result.failure(RuntimeException("Response OK but Data is null"))
            } else {
                Log.d("tag", "OK")
                Result.success(body.data)
            }
        }
        //잘못된 리턴(200X 오류 등)
        else {
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("tag", "비상: $errMsg")
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception) {
        //오류
        Result.failure(e)

    }
}