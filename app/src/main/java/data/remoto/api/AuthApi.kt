package com.clinia.app.data.remoto.api

import com.clinia.app.data.remoto.dto.LoginRequest
import com.clinia.app.data.remoto.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body

interface AuthApi {

    // ✅ Usar nombre calificado evita choques con otro POST
    @retrofit2.http.POST("auth/login")
    suspend fun login(
        @Body req: LoginRequest
    ): Response<LoginResponse>
}
