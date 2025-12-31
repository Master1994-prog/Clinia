package com.clinia.app.data.local.consultas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consultas")
data class ConsultaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val pacienteId: Long,
    val pacienteNombre: String,
    val dxCie10: String,
    val dxDescripcion: String,
    val notas: String,
    val fechaHoraMillis: Long = System.currentTimeMillis()
)
