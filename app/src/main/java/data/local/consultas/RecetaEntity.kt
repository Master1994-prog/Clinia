package com.clinia.app.data.local.consultas

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recetas",
    foreignKeys = [
        ForeignKey(
            entity = ConsultaEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("consultaId")]
)
data class RecetaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val consultaId: Long,
    val dci: String,
    val forma: String,
    val dosis: String,
    val frecuencia: String,
    val duracion: String,
    val warnings: String // guardamos como texto simple (joinToString)
)
