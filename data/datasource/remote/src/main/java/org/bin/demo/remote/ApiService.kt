package org.bin.demo.remote

import org.bin.demo.request.LoginReqDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/api/v1/auth/login")
    suspend fun login(@Body loginReqDto: LoginReqDto): Response<Unit>

}
