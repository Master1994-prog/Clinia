package com.clinia.app.data.remoto.dto

data class LoginRequest(val cmp: String, val password: String)

data class LoginResponse(
    val id: Long,
    val rol: String,
    val nombres: String,
    val apellidos: String,
    val cmp: String
)
