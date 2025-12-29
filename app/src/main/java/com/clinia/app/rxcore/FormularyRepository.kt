package com.clinia.app.rxcore

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FormularyRepository(private val context: Context) {

    suspend fun loadFromAssets(assetName: String = "formulary.json"): List<MedicationRule> {
        return withContext(Dispatchers.IO) {
            val jsonText = context.assets.open(assetName)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            val root = JSONObject(jsonText)
            val medsArr = root.getJSONArray("medications")

            fun arrStr(obj: JSONObject, key: String): List<String> {
                if (!obj.has(key)) return emptyList()
                val a = obj.getJSONArray(key)
                return List(a.length()) { idx -> a.getString(idx) }
            }

            val out = ArrayList<MedicationRule>(medsArr.length())
            for (i in 0 until medsArr.length()) {
                val m = medsArr.getJSONObject(i)

                out += MedicationRule(
                    id = m.getString("id"),
                    name = m.getString("name"),
                    form = m.getString("form"),
                    indications = arrStr(m, "indications"),
                    adultDose = m.getString("adultDose"),
                    adultFrequency = m.getString("adultFrequency"),
                    adultDuration = m.getString("adultDuration"),
                    pediatricDosePerKg = m.getString("pediatricDosePerKg"),
                    pediatricFrequency = m.getString("pediatricFrequency"),
                    pediatricDuration = m.getString("pediatricDuration"),
                    minAgeYears = m.optInt("minAgeYears", 0),
                    maxAgeYears = m.optInt("maxAgeYears", 120),
                    pregnancyAllowed = m.optBoolean("pregnancyAllowed", true),
                    contraindications = arrStr(m, "contraindications"),
                    warnings = arrStr(m, "warnings"),
                    requires = arrStr(m, "requires")
                )
            }
            out
        }
    }
}
