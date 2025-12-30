package com.clinia.app.ui.consultas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clinia.app.data.local.consultas.ConsultaEntity
import com.clinia.app.data.local.consultas.ConsultasRepository
import com.clinia.app.data.local.consultas.RecetaEntity
import com.clinia.app.data.local.consultas.RxDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConsultasState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val consultas: List<ConsultaEntity> = emptyList(),
    val recetaMap: Map<Long, RecetaEntity?> = emptyMap(),
    val lastSavedId: Long? = null
)

class ConsultasViewModel(
    private val repo: ConsultasRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConsultasState())
    val state: StateFlow<ConsultasState> = _state

    fun cargar(pacienteId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching {
                val cs = repo.listarConsultasPaciente(pacienteId)
                val rmap = cs.associate { it.id to repo.recetaDeConsulta(it.id) }

                _state.update {
                    it.copy(
                        isLoading = false,
                        consultas = cs,
                        recetaMap = rmap
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar consultas"
                    )
                }
            }
        }
    }

    fun guardar(
        pacienteId: Int,
        pacienteNombre: String,
        dxCie10: String,
        dxDescripcion: String,
        notas: String,
        rx: RxDraft?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, lastSavedId = null) }

            runCatching {
                val id = repo.guardarConsultaConReceta(
                    pacienteId = pacienteId,
                    pacienteNombre = pacienteNombre,
                    dxCie10 = dxCie10,
                    dxDescripcion = dxDescripcion,
                    notas = notas,
                    rx = rx
                )

                _state.update { it.copy(isLoading = false, lastSavedId = id) }

                // refresca lista
                cargar(pacienteId)
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al guardar consulta"
                    )
                }
            }
        }
    }

    fun clearSavedFlag() {
        _state.update { it.copy(lastSavedId = null) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
