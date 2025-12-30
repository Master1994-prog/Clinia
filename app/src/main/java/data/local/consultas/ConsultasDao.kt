package com.clinia.app.data.local.consultas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConsultasDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsulta(c: ConsultaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceta(r: RecetaEntity): Long

    @Query("""
        SELECT * FROM consultas
        WHERE pacienteId = :pacienteId
        ORDER BY createdAtMillis DESC
    """)
    suspend fun listConsultasByPaciente(pacienteId: Int): List<ConsultaEntity>

    @Query("""
        SELECT * FROM recetas
        WHERE consultaId = :consultaId
        LIMIT 1
    """)
    suspend fun getRecetaByConsulta(consultaId: Long): RecetaEntity?
}
