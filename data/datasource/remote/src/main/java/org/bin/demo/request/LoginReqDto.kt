package org.bin.demo.request

data class LoginReqDto(
    val userId: String,
    val password: String,
    val accessType: String,
)
