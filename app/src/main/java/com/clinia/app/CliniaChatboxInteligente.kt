package com.clinia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clinia.app.CodigoCieX.Cie10Item
import com.clinia.app.CodigoCieX.Cie10Picker
import com.clinia.app.CodigoCieX.Cie10Repository
import com.clinia.app.PetitorioPNUME.PnumRepository
import com.clinia.app.rxcore.CliniaRxEngine
import com.clinia.app.rxcore.FormularyRepository
import com.clinia.app.rxcore.MedicationRule
import com.clinia.app.rxcore.PatientContext
import com.clinia.app.rxcore.RxSuggestion
import org.json.JSONObject

@Composable
fun CliniaChatboxInteligente() {

    val bg = Color(0xFFF4F7FB)
    val title = Color(0xFF0E1A2B)
    val primary = Color(0xFF1E88A8)

    val context = LocalContext.current

    val cieRepo = remember { Cie10Repository(context) }
    val pnumRepo = remember { PnumRepository(context) }
    val formularyRepo = remember { FormularyRepository(context) }

    var ready by remember { mutableStateOf(false) }

    var allowedDci by remember { mutableStateOf(emptySet<String>()) }
    var formulary by remember { mutableStateOf(emptyList<MedicationRule>()) }
    var dxRules by remember { mutableStateOf(emptyList<DxRule>()) }

    LaunchedEffect(Unit) {
        // CIE10
        cieRepo.ensureLoaded()

        // PNUME allowlist
        pnumRepo.ensureLoaded("pnume.csv")
        allowedDci = pnumRepo.allowedDciSet()

        // Formulary (generado desde PNUME)
        formulary = formularyRepo.loadFromAssets("formulary.json")

        // Dx rules (DX -> lista de DCI sugeridas)
        dxRules = DxRulesRepository.loadFromAssets(context, "dx_rules.json")

        ready = true
    }

    // DX (CIE-10)
    var dxQuery by remember { mutableStateOf("") }
    var selectedDx by remember { mutableStateOf<Cie10Item?>(null) }
    val dxResults = remember(dxQuery, ready) {
        if (ready) cieRepo.search(dxQuery, 25) else emptyList()
    }

    // Datos clínicos
    var sexo by remember { mutableStateOf("F") }
    var edad by remember { mutableStateOf("") }
    var embarazada by remember { mutableStateOf(false) }
    var peso by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var spo2 by remember { mutableStateOf("") }
    var ram by remember { mutableStateOf("") } // tags: alergia_penicilina, etc.

    // Resultados
    var suggestions by remember { mutableStateOf(emptyList<RxSuggestion>()) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Chatbot inteligente",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = title
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Sugerencias filtradas por DX (CIE10) + PNUME (MINSA).",
            color = title.copy(alpha = 0.70f)
        )

        Spacer(Modifier.height(12.dp))

        if (!ready) {
            Text("Cargando catálogos (CIE10 + PNUME + reglas + formulary)...", color = title.copy(alpha = 0.7f))
            return@Column
        }

        // Caja DX + datos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("Diagnóstico (CIE-10)", fontWeight = FontWeight.Bold, color = title)
            Spacer(Modifier.height(10.dp))

            Cie10Picker(
                query = dxQuery,
                onQueryChange = {
                    dxQuery = it
                    selectedDx = null
                },
                results = dxResults,
                onPick = { picked ->
                    selectedDx = picked
                    dxQuery = "${picked.codigo} - ${picked.descripcion}"
                }
            )

            Spacer(Modifier.height(14.dp))
            Text("Datos clínicos", fontWeight = FontWeight.Bold, color = title)
            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = sexo,
                    onValueChange = { sexo = it.take(1).uppercase() },
                    label = { Text("Sexo (F/M)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it.filter(Char::isDigit).take(3) },
                    label = { Text("Edad") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it.take(6) },
                    label = { Text("Peso (kg)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = talla,
                    onValueChange = { talla = it.take(6) },
                    label = { Text("Talla (cm)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = spo2,
                onValueChange = { spo2 = it.filter(Char::isDigit).take(3) },
                label = { Text("SpO2 (%)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = ram,
                onValueChange = { ram = it },
                label = { Text("RAM/Alergias (tags) ej: alergia_penicilina") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = embarazada, onCheckedChange = { embarazada = it })
                Text("Embarazada", color = title)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Botón generar
        Button(
            onClick = {
                error = null
                suggestions = emptyList()

                val dx = selectedDx
                if (dx == null) {
                    error = "Selecciona un diagnóstico (CIE-10)."
                    return@Button
                }

                val edadInt = edad.toIntOrNull()
                val pesoD = peso.toDoubleOrNull()
                val tallaD = talla.toDoubleOrNull()
                val spo2I = spo2.toIntOrNull()

                val ramTags = ram.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                val patient = PatientContext(
                    dxCode = dx.codigo,
                    dxDescription = dx.descripcion,
                    sexo = sexo,
                    edadYears = edadInt,
                    embarazada = if (sexo.uppercase() == "F") embarazada else false,
                    pesoKg = pesoD,
                    tallaCm = tallaD,
                    spo2 = spo2I,
                    ramTags = ramTags
                )

                // ✅ 1) DCI sugeridas por reglas del DX
                val rule = DxRulesRepository.pickRule(dx.codigo, dx.descripcion, dxRules)
                val dciForDx = rule?.recommendedDci
                    ?.map { normalize(it) }
                    ?.toSet()
                    ?: emptySet()

                // ✅ 2) Filtrar formulary por esas DCI (si hay regla)
                val filteredFormulary = if (dciForDx.isNotEmpty()) {
                    formulary.filter { normalize(it.name) in dciForDx }
                } else {
                    formulary
                }

                val result = CliniaRxEngine.suggest(
                    patient = patient,
                    formulary = filteredFormulary,
                    allowedDci = allowedDci,
                    limit = 6
                )

                suggestions = result

                if (result.isEmpty()) {
                    error = "No hay alternativas que cumplan DX + PNUME + reglas actuales."
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Generar alternativas", color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (error != null) {
            Spacer(Modifier.height(10.dp))
            Text(error!!, color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(14.dp))

        if (suggestions.isNotEmpty()) {
            Text("Opciones sugeridas", fontWeight = FontWeight.Bold, color = title)
            Spacer(Modifier.height(10.dp))

            suggestions.forEach { s ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(14.dp)
                ) {
                    Text("${s.dci}", fontWeight = FontWeight.Bold, color = title)
                    Spacer(Modifier.height(6.dp))
                    Text("Presentación: ${s.form}", color = title.copy(alpha = 0.85f))
                    Text("Dosis: ${s.dose}", color = title.copy(alpha = 0.85f))
                    Text("Frecuencia: ${s.frequency}", color = title.copy(alpha = 0.85f))
                    Text("Duración: ${s.duration}", color = title.copy(alpha = 0.85f))

                    if (s.warnings.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Consideraciones:", fontWeight = FontWeight.Bold, color = title)
                        s.warnings.forEach { w ->
                            Text("• $w", color = title.copy(alpha = 0.80f))
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(18.dp))
    }
}

/* =========================
   Dx Rules Loader (assets)
   ========================= */

data class DxRule(
    val cie10: String,
    val keywords: List<String>,
    val recommendedDci: List<String>,
    val notes: String
)

object DxRulesRepository {

    fun loadFromAssets(context: android.content.Context, assetName: String): List<DxRule> {
        val jsonText = context.assets.open(assetName)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val root = JSONObject(jsonText)
        val arr = root.getJSONArray("rules")

        val out = ArrayList<DxRule>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val kwArr = o.getJSONArray("keywords")
            val dciArr = o.getJSONArray("recommendedDci")

            val keywords = List(kwArr.length()) { idx -> kwArr.getString(idx) }
            val dci = List(dciArr.length()) { idx -> dciArr.getString(idx) }

            out += DxRule(
                cie10 = o.getString("cie10"),
                keywords = keywords,
                recommendedDci = dci,
                notes = o.optString("notes", "")
            )
        }
        return out
    }

    fun pickRule(dxCode: String, dxDesc: String, rules: List<DxRule>): DxRule? {
        val codeN = normalize(dxCode)
        val descN = normalize(dxDesc)

        // 1) Match por código exacto
        rules.firstOrNull { normalize(it.cie10) == codeN }?.let { return it }

        // 2) Fallback por keywords dentro de la descripción
        return rules.firstOrNull { rule ->
            rule.keywords.any { k -> descN.contains(normalize(k)) }
        }
    }
}

private fun normalize(s: String): String =
    s.trim().lowercase()
        .replace("á","a").replace("é","e").replace("í","i")
        .replace("ó","o").replace("ú","u").replace("ñ","n")
