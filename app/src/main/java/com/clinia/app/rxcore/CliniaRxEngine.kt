package com.clinia.app.rxcore

object CliniaRxEngine {

    fun suggest(
        patient: PatientContext,
        formulary: List<MedicationRule>,
        allowedDci: Set<String>,
        limit: Int = 6
    ): List<RxSuggestion> {

        val dxCodeNorm = normalize(patient.dxCode)
        val dxDescNorm = normalize(patient.dxDescription)
        val ramNorm = patient.ramTags.map { normalize(it) }.toSet()

        val candidates = formulary.filter { med ->

            // 0) ✅ PNUME allowlist (solo DCI permitida)
            val medDci = normalize(med.name)
            if (!allowedDci.contains(medDci)) return@filter false

            // 1) ✅ Match Dx: por código o por keyword en descripción
            val matchDx = med.indications.isEmpty() || med.indications.any { ind ->
                val indN = normalize(ind)
                indN == dxCodeNorm || (indN.isNotBlank() && dxDescNorm.contains(indN))
            }
            if (!matchDx) return@filter false

            // 2) Edad
            val edad = patient.edadYears
            val ageOk = (edad == null) || (edad in med.minAgeYears..med.maxAgeYears)
            if (!ageOk) return@filter false

            // 3) Embarazo
            val preg = patient.embarazada
            val pregOk = (preg == null) || (!preg || med.pregnancyAllowed)
            if (!pregOk) return@filter false

            // 4) Contraindicaciones por RAM tags
            val contraindicated = med.contraindications.any { normalize(it) in ramNorm }
            if (contraindicated) return@filter false

            // 5) Requeridos (peso)
            val requiresWeight = med.requires.any { normalize(it) == "peso" }
            if (requiresWeight && patient.pesoKg == null) return@filter false

            true
        }

        return candidates.take(limit).map { med ->
            val isPediatric = (patient.edadYears != null && patient.edadYears < 12)

            val dose = if (isPediatric) med.pediatricDosePerKg else med.adultDose
            val freq = if (isPediatric) med.pediatricFrequency else med.adultFrequency
            val dur  = if (isPediatric) med.pediatricDuration else med.adultDuration

            RxSuggestion(
                dci = med.name,
                form = med.form,
                dose = dose,
                frequency = freq,
                duration = dur,
                warnings = med.warnings
            )
        }
    }

    private fun normalize(s: String): String =
        s.trim().lowercase()
            .replace("á","a").replace("é","e").replace("í","i")
            .replace("ó","o").replace("ú","u").replace("ñ","n")
}
