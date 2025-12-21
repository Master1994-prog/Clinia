package com.clinia.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DoctorDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(doctor: DoctorEntity): Long

    // LOGIN: código + contraseña (hash)
    @Query("""
        SELECT * FROM doctores 
        WHERE codigo = :codigo AND passwordHash = :passwordHash
        LIMIT 1
    """)
    suspend fun login(codigo: String, passwordHash: String): DoctorEntity?

    // Para evitar duplicados por CMP en registro
    @Query("SELECT * FROM doctores WHERE cmp = :cmp LIMIT 1")
    suspend fun findByCmp(cmp: String): DoctorEntity?

    // Opcional: buscar por código
    @Query("SELECT * FROM doctores WHERE codigo = :codigo LIMIT 1")
    suspend fun findByCodigo(codigo: String): DoctorEntity?
}
