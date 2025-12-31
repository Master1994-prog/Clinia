package com.clinia.app.pacientes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object PacienteStore {
    var pacienteActivo: Paciente? by mutableStateOf(null)
    fun clear() {
        pacienteActivo = null
    }
}
