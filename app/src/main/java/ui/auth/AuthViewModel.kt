package com.clinia.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clinia.app.data.local.DoctorEntity
import com.clinia.app.data.local.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Locale

// Estado del login
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

    fun clearMessages() {
        _state.value = _state.value.copy(error = null)
    }

    // 🔐 LOGIN (botón INGRESAR)
    fun login(codigo: String, passwordRaw: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val code = codigo.trim()
            val passHash = passwordRaw.trim().sha256()   // ✅ AQUÍ SE HASHEA

            val doctor = repository.login(code, passHash)

            if (doctor != null && doctor.activo) {
                _state.update {
                    it.copy(
                        loggedDoctor = doctor,
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        loggedDoctor = null,
                        isLoading = false,
                        error = "Credenciales inválidas"
                    )
                }
            }
        }
    }

    /**
     * ✅ LOGOUT REAL
     * Limpia completamente la sesión
     */
    fun logout() {
        _state.update {
            AuthState() // ← estado limpio, loggedDoctor = null
        }
    }

    /**
     * Opcional: limpiar error manualmente
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

}

// 🔑 hash simple (para prototipo)
private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
