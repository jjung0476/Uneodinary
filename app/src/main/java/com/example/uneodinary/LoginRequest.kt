package com.example.uneodinary

data class LoginRequest(
    val reportId: Int,
    val tagName: String,
    val reportData: String,
    val managerName: String,
    val managerAccount: String
)
