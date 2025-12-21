package com.clinia.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clinia.app.data.local.DoctorEntity
import com.clinia.app.data.local.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Locale

// Estado del login
data class AuthState(
    val loading: Boolean = false,
    val error: String? = null,
    val loggedDoctor: DoctorEntity? = null
)

class AuthViewModel(
    private val repo: DoctorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun clearMessages() {
        _state.value = _state.value.copy(error = null)
    }

    // 🔐 LOGIN (botón INGRESAR)
    fun login(codigo: String, password: String) {
        val cod = codigo.trim().uppercase(Locale.getDefault())
        val pass = password.trim()

        if (cod.isEmpty() || pass.isEmpty()) {
            _state.value = _state.value.copy(error = "Ingresa código y contraseña")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState(loading = true)

            val doctor = repo.login(cod, pass.sha256())

            _state.value = when {
                doctor == null ->
                    AuthState(error = "Código o contraseña incorrectos")

                !doctor.activo ->
                    AuthState(error = "Usuario desactivado")

                else ->
                    AuthState(loggedDoctor = doctor)
            }
        }
    }
}

// 🔑 hash simple (para prototipo)
private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
