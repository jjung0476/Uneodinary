package com.example.uneodinary

data class AuthResponse<T>(
    val isSuccess : Boolean,
    val code : String,
    val message : String,
    val data : T? = null
)