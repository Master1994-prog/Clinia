package com.clinia.app.data.local.consultas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ConsultasState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val lastSavedId: Long? = null
)

class ConsultasViewModel(private val repo: ConsultasRepository) : ViewModel() {

    val consultas: StateFlow<List<ConsultaConReceta>> =
        repo.observeConsultas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(ConsultasState())
    val state: StateFlow<ConsultasState> = _state

    fun clearSavedFlag() {
        _state.update { it.copy(lastSavedId = null) }
    }

    fun guardar(
        pacienteId: Long,
        pacienteNombre: String,
        dxCie10: String,
        dxDescripcion: String,
        notas: String,
        rx: RxDraft
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            runCatching {
                repo.guardarConsultaConReceta(
                    pacienteId, pacienteNombre, dxCie10, dxDescripcion, notas, rx
                )
            }.onSuccess { id ->
                _state.update { it.copy(isSaving = false, lastSavedId = id) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message ?: "Error al guardar") }
            }
        }
    }
}
