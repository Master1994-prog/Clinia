package com.clinia.app.data.local.consultas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class RecetaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val consultaId: Long,
    val dci: String,
    val forma: String,
    val dosis: String,
    val frecuencia: String,
    val duracion: String,
    val warningsJson: String = "[]"
)
