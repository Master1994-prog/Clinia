package com.clinia.app.pacientes

data class Paciente(
    val id: Int,
    val nombres: String,
    val edad: Int,
    val sexo: String,
    val peso: Double?,
    val talla: Double?,
    val embarazada: Boolean?,
    val ram: List<String>
)
