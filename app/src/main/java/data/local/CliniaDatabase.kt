package com.clinia.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DoctorEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CliniaDatabase : RoomDatabase() {

    abstract fun doctorDao(): DoctorDao

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
