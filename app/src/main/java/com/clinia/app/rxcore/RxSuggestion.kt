package com.clinia.app.rxcore

/**
 * Resultado final que se muestra en UI y se guarda en Room.
 * OJO: los nombres están alineados a tu CliniaChatboxInteligente.kt:
 * s.dci, s.form, s.dose, s.frequency, s.duration, s.warnings
 */
data class RxSuggestion(
    val dci: String,
    val form: String,
    val dose: String,
    val frequency: String,
    val duration: String,
    val warnings: List<String> = emptyList(),
    val contraindications: List<String> = emptyList()
)
