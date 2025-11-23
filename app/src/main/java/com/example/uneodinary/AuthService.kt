package com.example.uneodinary

import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @POST("login")
    suspend fun login(
        @Body req: LoginRequest
    ): Response<AuthResponse<List<Report>>>
}