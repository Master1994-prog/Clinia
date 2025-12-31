package com.clinia.app.data.local.consultas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultasDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsulta(c: ConsultaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceta(r: RecetaEntity): Long

    @Transaction
    suspend fun insertConsultaConReceta(c: ConsultaEntity, r: RecetaEntity): Long {
        val consultaId = insertConsulta(c)
        insertReceta(r.copy(consultaId = consultaId))
        return consultaId
    }

    @Transaction
    @Query("SELECT * FROM consultas ORDER BY fechaHoraMillis DESC")
    fun observeConsultas(): Flow<List<ConsultaConReceta>>
}
