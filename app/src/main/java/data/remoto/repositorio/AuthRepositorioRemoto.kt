package com.clinia.app.data.remoto.repositorio

import com.clinia.app.data.remoto.api.AuthApi
import com.clinia.app.data.remoto.dto.LoginRequest
import com.clinia.app.data.remoto.dto.LoginResponse
import com.clinia.app.data.remoto.nucleo.ResultadoRed

class AuthRepositorioRemoto(
    private val api: AuthApi
) {
    suspend fun login(cmp: String, password: String): ResultadoRed<LoginResponse> {
        return try {
            val r = api.login(LoginRequest(cmp = cmp.trim(), password = password))
            if (r.isSuccessful && r.body() != null) {
                ResultadoRed.Exito(r.body()!!)
            } else {
                ResultadoRed.Error(
                    codigo = r.code(),
                    mensaje = r.errorBody()?.string() ?: "Error HTTP"
                )
            }
        } catch (e: Exception) {
            ResultadoRed.Error(mensaje = e.message ?: "Error de red")
        }
    }
}
