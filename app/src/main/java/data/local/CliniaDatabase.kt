package com.clinia.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.clinia.app.data.local.consultas.ConsultaEntity
import com.clinia.app.data.local.consultas.ConsultasDao
import com.clinia.app.data.local.consultas.RecetaEntity

@Database(
    entities = [
        DoctorEntity::class,
        ConsultaEntity::class,
        RecetaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CliniaDatabase : RoomDatabase() {

    abstract fun doctorDao(): DoctorDao

    // ✅ NUEVO
    abstract fun consultasDao(): ConsultasDao

    companion object {
        @Volatile private var INSTANCE: CliniaDatabase? = null

        fun get(context: Context): CliniaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CliniaDatabase::class.java,
                    "clinia.db"
                )
                    .fallbackToDestructiveMigration() // dev: si cambias campos, no crashea
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
