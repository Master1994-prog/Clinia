package com.clinia.app.data.local.consultas

import kotlinx.coroutines.flow.Flow

class ConsultasRepository(private val dao: ConsultasDao) {

    fun observeConsultas(): Flow<List<ConsultaConReceta>> = dao.observeConsultas()

    suspend fun guardarConsultaConReceta(
        pacienteId: Long,
        pacienteNombre: String,
        dxCie10: String,
        dxDescripcion: String,
        notas: String,
        rx: RxDraft
    ): Long {
        val consulta = ConsultaEntity(
            pacienteId = pacienteId,
            pacienteNombre = pacienteNombre,
            dxCie10 = dxCie10,
            dxDescripcion = dxDescripcion,
            notas = notas
        )

        val receta = RecetaEntity(
            consultaId = 0L,
            dci = rx.dci,
            forma = rx.forma,
            dosis = rx.dosis,
            frecuencia = rx.frecuencia,
            duracion = rx.duracion,
            warnings = rx.warnings.joinToString("; ")
        )

        return dao.insertConsultaConReceta(consulta, receta)
    }
}

data class RxDraft(
    val dci: String,
    val forma: String,
    val dosis: String,
    val frecuencia: String,
    val duracion: String,
    val warnings: List<String>
)
