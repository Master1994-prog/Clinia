package com.clinia.app.data.local

import com.clinia.app.data.local.DoctorDao
import com.clinia.app.data.local.DoctorEntity

class DoctorRepository(
    private val dao: DoctorDao
) {

    suspend fun registerDoctor(doctor: DoctorEntity): Result<Long> =
        runCatching {
            dao.insert(doctor)
        }

    suspend fun login(
        codigo: String,
        passwordHash: String
    ): DoctorEntity? {
        return dao.login(codigo, passwordHash)
    }

    suspend fun existsCmp(cmp: String): Boolean {
        return dao.findByCmp(cmp) != null
    }
}
