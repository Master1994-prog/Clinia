package com.clinia.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clinia.app.data.local.DoctorEntity
import com.clinia.app.data.local.DoctorRepository
import com.clinia.app.data.remoto.api.AuthApi
import com.clinia.app.data.remoto.nucleo.ClienteApi
import com.clinia.app.data.remoto.nucleo.ResultadoRed
import com.clinia.app.data.remoto.repositorio.AuthRepositorioRemoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest

data class AuthState(
    val loggedDoctor: DoctorEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repository: DoctorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    // 🔌 Wiring remoto (sin Hilt)
    private val authApi: AuthApi = ClienteApi.retrofit.create(AuthApi::class.java)
    private val authRemoto = AuthRepositorioRemoto(authApi)

    fun clearMessages() {
        _state.value = _state.value.copy(error = null)
    }

    /**
     * ✅ LOGIN REMOTO (API → MySQL)
     * En tu UI el campo puede llamarse "codigo", pero aquí lo tratamos como CMP.
     */
    fun login(codigoOCmp: String, passwordRaw: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val cmp = codigoOCmp.trim()
            val passPlain = passwordRaw.trim()

            when (val r = authRemoto.login(cmp = cmp, password = passPlain)) {
                is ResultadoRed.Exito -> {
                    val dto = r.datos

                    // Mapeo a entidad local (Room)
                    val doctorLocal = DoctorEntity(
                        id = dto.id, // si tu Room usa autogenerate, puedes poner 0 y guardar dto.id aparte
                        nombres = dto.nombres,
                        apellidos = dto.apellidos,
                        cmp = dto.cmp,
                        codigo = dto.cmp, // 👈 clave: usamos CMP también como "codigo" en app
                        passwordHash = passPlain.sha256(),
                        activo = true
                    )

                    // Guardar/actualizar local (si tu repo tiene registerDoctor/insert)
                    runCatching {
                        repository.registerDoctor(doctorLocal)
                    }

                    _state.update {
                        it.copy(
                            loggedDoctor = doctorLocal,
                            isLoading = false,
                            error = null
                        )
                    }
                }

                is ResultadoRed.Error -> {
                    _state.update {
                        it.copy(
                            loggedDoctor = null,
                            isLoading = false,
                            error = r.mensaje
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        _state.update { AuthState() }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
