package com.clinia.app.data.local.consultas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consultas")
data class ConsultaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pacienteId: Int,
    val pacienteNombre: String,
    val dxCie10: String,
    val dxDescripcion: String,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val notas: String = ""
)
