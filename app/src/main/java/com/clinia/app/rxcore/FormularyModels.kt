package com.clinia.app.rxcore

data class MedicationRule(
    val id: String,
    val name: String,                 // DCI (ej: "AMOXICILINA")
    val form: String,
    val indications: List<String>,     // CIE10 ("J02.9") o keywords ("faringitis")
    val adultDose: String,
    val adultFrequency: String,
    val adultDuration: String,
    val pediatricDosePerKg: String,
    val pediatricFrequency: String,
    val pediatricDuration: String,
    val minAgeYears: Int,
    val maxAgeYears: Int,
    val pregnancyAllowed: Boolean,
    val contraindications: List<String>,
    val warnings: List<String>,
    val requires: List<String>
)

data class PatientContext(
    val dxCode: String,
    val dxDescription: String,
    val sexo: String,                 // "F" o "M"
    val edadYears: Int?,
    val embarazada: Boolean?,
    val pesoKg: Double?,
    val tallaCm: Double?,
    val spo2: Int?,
    val ramTags: List<String>         // ej: ["alergia_penicilina"]
)

data class RxSuggestion(
    val dci: String,
    val form: String,
    val dose: String,
    val frequency: String,
    val duration: String,
    val warnings: List<String>
)
