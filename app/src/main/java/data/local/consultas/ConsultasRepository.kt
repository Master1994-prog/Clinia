package com.clinia.app.data.local.consultas

import org.json.JSONArray

class ConsultasRepository(
    private val dao: ConsultasDao
) {

    /**
     * Guarda:
     * - Consulta (cabecera)
     * - Receta (1 receta por consulta, si rx != null)
     * Retorna el ID de la consulta creada.
     */
    suspend fun guardarConsultaConReceta(
        pacienteId: Int,
        pacienteNombre: String,
        dxCie10: String,
        dxDescripcion: String,
        notas: String,
        rx: RxDraft?
    ): Long {

        val consultaId = dao.insertConsulta(
            ConsultaEntity(
                pacienteId = pacienteId,
                pacienteNombre = pacienteNombre,
                dxCie10 = dxCie10,
                dxDescripcion = dxDescripcion,
                notas = notas
            )
        )

        if (rx != null) {
            dao.insertReceta(
                RecetaEntity(
                    consultaId = consultaId,
                    dci = rx.dci,
                    forma = rx.forma,
                    dosis = rx.dosis,
                    frecuencia = rx.frecuencia,
                    duracion = rx.duracion,
                    warningsJson = JSONArray(rx.warnings).toString()
                )
            )
        }

        return consultaId
    }

    suspend fun listarConsultasPaciente(pacienteId: Int): List<ConsultaEntity> =
        dao.listConsultasByPaciente(pacienteId)

    suspend fun recetaDeConsulta(consultaId: Long): RecetaEntity? =
        dao.getRecetaByConsulta(consultaId)
}

/**
 * “Borrador” de receta que viene desde el Chatbot (RxSuggestion -> RxDraft)
 */
data class RxDraft(
    val dci: String,
    val forma: String,
    val dosis: String,
    val frecuencia: String,
    val duracion: String,
    val warnings: List<String>
)
