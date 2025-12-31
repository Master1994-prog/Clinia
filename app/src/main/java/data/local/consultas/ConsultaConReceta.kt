package com.clinia.app.data.local.consultas

import androidx.room.Embedded
import androidx.room.Relation

data class ConsultaConReceta(
    @Embedded val consulta: ConsultaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "consultaId"
    )
    val recetas: List<RecetaEntity>
)
