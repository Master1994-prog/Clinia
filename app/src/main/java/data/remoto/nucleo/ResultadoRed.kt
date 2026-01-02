package com.clinia.app.data.remoto.nucleo

sealed class ResultadoRed<out T> {
    data class Exito<T>(val datos: T) : ResultadoRed<T>()
    data class Error(val codigo: Int? = null, val mensaje: String) : ResultadoRed<Nothing>()
}
