package com.clinia.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "doctores",
    indices = [
        Index(value = ["codigo"], unique = true),
        Index(value = ["cmp"], unique = true)
    ]
)
data class DoctorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombres: String,
    val apellidos: String,
    val cmp: String,
    val codigo: String,
    val passwordHash: String,
    val activo: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
