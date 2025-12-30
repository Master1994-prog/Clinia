package com.clinia.app.rxcore

object CliniaRxEngine {

    /**
     * Genera sugerencias filtradas por:
     * - allowlist PNUME (allowedDci)
     * - edad / embarazo
     * - RAM tags (contraindicaciones)
     * - requerimientos mínimos (ej: peso)
     *
     * Se llama tal cual lo tienes en tu UI:
     * CliniaRxEngine.suggest(patient, filteredFormulary, allowedDci, limit)
     */
    fun suggest(
        patient: PatientContext,
        formulary: List<MedicationRule>,
        allowedDci: Set<String>,
        limit: Int = 6
    ): List<RxSuggestion> {

        val allowedNorm = allowedDci.map { normalize(it) }.toHashSet()

        val edad = patient.edadYears ?: -1
        val esPediatrico = edad in 0..11
        val esAdulto = edad >= 12
        val estaEmbarazada = patient.embarazada == true
        val ramNorm = patient.ramTags.map { normalize(it) }.toHashSet()

        val candidates = formulary
            // 1) Solo lo permitido por PNUME (allowlist por DCI)
            .filter { normalize(it.name) in allowedNorm }
            // 2) Filtros básicos por edad
            .filter { rule ->
                val minOk = rule.minAgeYears?.let { edad == -1 || edad >= it } ?: true
                val maxOk = rule.maxAgeYears?.let { edad == -1 || edad <= it } ?: true
                minOk && maxOk
            }
            // 3) Embarazo
            .filter { rule ->
                if (!estaEmbarazada) true else (rule.pregnancyAllowed != false)
            }
            // 4) Contraindicaciones: si la RAM del paciente coincide con contraindicaciones del medicamento -> bloquear
            .filter { rule ->
                val contra = rule.contraindications.map { normalize(it) }.toHashSet()
                // si hay intersección, NO pasa
                contra.intersect(ramNorm).isEmpty()
            }
            // 5) Requerimientos mínimos
            .filter { rule ->
                val req = rule.requires.map { normalize(it) }.toHashSet()
                val requierePeso = "peso" in req || "pesokg" in req
                if (!requierePeso) true else (patient.pesoKg != null)
            }

        val out = ArrayList<RxSuggestion>(limit)

        for (med in candidates) {
            if (out.size >= limit) break

            val (dose, freq, dur) = when {
                esPediatrico -> pediatricTriple(med, patient)
                esAdulto -> adultTriple(med)
                else -> adultTriple(med) // si no hay edad, asumimos adulto
            }

            // si pediátrico y no se pudo calcular por falta de peso, saltar
            if (esPediatrico && dose.startsWith("FALTA_PESO")) continue

            out += RxSuggestion(
                dci = med.name,
                form = med.form,
                dose = dose,
                frequency = freq,
                duration = dur,
                warnings = med.warnings,
                contraindications = med.contraindications
            )
        }

        return out
    }

    // -------------------------
    // Helpers (dosis)
    // -------------------------

    private fun adultTriple(med: MedicationRule): Triple<String, String, String> {
        val d = med.adultDose ?: "Según guía clínica"
        val f = med.adultFrequency ?: "Según guía clínica"
        val du = med.adultDuration ?: "Según guía clínica"
        return Triple(d, f, du)
    }

    private fun pediatricTriple(med: MedicationRule, patient: PatientContext): Triple<String, String, String> {
        val peso = patient.pesoKg
        if (peso == null) {
            // tu UI puede mostrarlo o simplemente lo filtramos arriba
            return Triple("FALTA_PESO", "FALTA_PESO", "FALTA_PESO")
        }

        val perKgText = med.pediatricDosePerKg ?: "Según guía clínica"
        val freq = med.pediatricFrequency ?: "Según guía clínica"
        val dur = med.pediatricDuration ?: "Según guía clínica"

        // Si viene algo tipo "40 mg/kg/día", intentamos calcular: 40 * peso = mg/día
        val mgPorKgPorDia = parseMgPerKgPerDay(perKgText)
        val dose = if (mgPorKgPorDia != null) {
            val totalMgDia = mgPorKgPorDia * peso
            "${totalMgDia.toInt()} mg/día (${perKgText})"
        } else {
            perKgText
        }

        return Triple(dose, freq, dur)
    }

    /**
     * Intenta extraer el primer número "mg/kg/día" del texto.
     * Ej: "40 mg/kg/día" -> 40.0
     */
    private fun parseMgPerKgPerDay(text: String): Double? {
        val t = text.lowercase()
        // busca patrón "numero mg/kg"
        val regex = Regex("""(\d+(\.\d+)?)\s*mg\s*/\s*kg""")
        val m = regex.find(t) ?: return null
        return m.groupValues[1].toDoubleOrNull()
    }

    private fun normalize(s: String): String =
        s.trim().lowercase()
            .replace("á","a").replace("é","e").replace("í","i")
            .replace("ó","o").replace("ú","u").replace("ñ","n")
}
