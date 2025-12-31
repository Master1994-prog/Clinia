package com.clinia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.clinia.app.CodigoCieX.Cie10Item
import com.clinia.app.CodigoCieX.Cie10Picker
import com.clinia.app.CodigoCieX.Cie10Repository
import com.clinia.app.PetitorioPNUME.PnumRepository
import com.clinia.app.data.local.CliniaDatabase
import com.clinia.app.data.local.consultas.ConsultasRepository
import com.clinia.app.data.local.consultas.ConsultasViewModel
import com.clinia.app.data.local.consultas.RxDraft
import com.clinia.app.pacientes.PacienteStore
import com.clinia.app.rxcore.CliniaRxEngine
import com.clinia.app.rxcore.FormularyRepository
import com.clinia.app.rxcore.PatientContext
import com.clinia.app.rxcore.RxSuggestion
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun CliniaChatboxInteligente(
    navController: NavHostController
) {

    val bg = Color(0xFFF4F7FB)
    val title = Color(0xFF0E1A2B)
    val primary = Color(0xFF1E88A8)

    val context = LocalContext.current

    // ✅ Paciente activo
    val paciente = PacienteStore.pacienteActivo
    if (paciente == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Selecciona un paciente en 'Pacientes' para usar el chatbot.",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.SemiBold
            )
        }
        return
    }

    // ✅ Room (guardar consulta)
    val db = remember { CliniaDatabase.get(context) }
    val consultasRepo = remember { ConsultasRepository(db.consultasDao()) }
    val vm = remember { ConsultasViewModel(consultasRepo) }
    val state by vm.state.collectAsState()

    // ✅ Catálogos
    val cieRepo = remember { Cie10Repository(context) }
    val pnumRepo = remember { PnumRepository(context) }
    val formularyRepo = remember { FormularyRepository(context) }

    var ready by remember { mutableStateOf(false) }
    var allowedDci by remember { mutableStateOf(emptySet<String>()) }
    var formularyRules by remember { mutableStateOf(emptyList<com.clinia.app.rxcore.MedicationRule>()) }
    var dxRules by remember { mutableStateOf(emptyList<DxRule>()) }

    LaunchedEffect(Unit) {
        // CIE10
        cieRepo.ensureLoaded()

        // PNUME (assets/pnume.csv)
        pnumRepo.ensureLoaded("pnume.csv")
        allowedDci = pnumRepo.allowedDciSet()

        // Formulary (assets/formulary.json)
        formularyRules = formularyRepo.loadFromAssets("formulary.json")

        // Reglas DX (assets/dx_rules.json)
        dxRules = DxRulesRepository.loadFromAssets(context, "dx_rules.json")

        ready = true
    }

    // ✅ UI state
    var dxQuery by remember { mutableStateOf("") }
    var selectedDx by remember { mutableStateOf<Cie10Item?>(null) }

    val dxResults = remember(dxQuery, ready) {
        if (ready) cieRepo.search(dxQuery, 25) else emptyList()
    }

    var suggestions by remember { mutableStateOf<List<RxSuggestion>>(emptyList()) }
    var selectedRx by remember { mutableStateOf<RxSuggestion?>(null) }
    var notas by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ✅ Toast tipo snackbar cuando se guarda
    LaunchedEffect(state.lastSavedId) {
        if (state.lastSavedId != null) {
            scope.launch { snackbar.showSnackbar("✅ Consulta guardada correctamente") }
            vm.clearSavedFlag()
            selectedRx = null
            notas = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SnackbarHost(hostState = snackbar)
        Spacer(Modifier.height(8.dp))

        Text(
            text = "Chatbot inteligente",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = title
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Paciente: ${paciente.nombres} • ${paciente.edad}a • ${paciente.sexo}",
            color = title.copy(alpha = 0.85f),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        if (!ready) {
            Text(
                text = "Cargando catálogos (CIE10 + PNUME + Formulary + Reglas)...",
                color = title.copy(alpha = 0.7f)
            )
            return@Column
        }

        Button(
            onClick = {
                // 🔁 Volver a la lista de pacientes
                PacienteStore.clear()
                navController.navigate(CliniaTabRoutes.PACIENTES) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E88E5) // azul Clinia
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Cambiar paciente",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // =========================
        // 1) DIAGNÓSTICO (CIE10)
        // =========================
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
                    suggestions = emptyList()
                    selectedRx = null
                    error = null
                },
                results = dxResults,
                onPick = { picked ->
                    selectedDx = picked
                    dxQuery = "${picked.codigo} - ${picked.descripcion}"
                    suggestions = emptyList()
                    selectedRx = null
                    error = null
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        // =========================
        // 2) GENERAR SUGERENCIAS
        // =========================
        Button(
            onClick = {
                error = null
                suggestions = emptyList()
                selectedRx = null

                val dx = selectedDx
                if (dx == null) {
                    error = "Selecciona un diagnóstico (CIE-10)."
                    return@Button
                }

                val patient = PatientContext(
                    dxCode = dx.codigo,
                    dxDescription = dx.descripcion,
                    sexo = paciente.sexo,
                    edadYears = paciente.edad,
                    embarazada = if (paciente.sexo == "F") (paciente.embarazada ?: false) else false,
                    pesoKg = paciente.peso,
                    tallaCm = paciente.talla,
                    spo2 = null,
                    ramTags = paciente.ram
                )

                // Regla por DX (si existe) para sugerir solo ciertos DCI
                val rule = DxRulesRepository.pickRule(dx.codigo, dx.descripcion, dxRules)
                val dciForDx = rule?.recommendedDci?.map { normalize(it) }?.toSet() ?: emptySet()

                val filteredFormulary = if (dciForDx.isNotEmpty()) {
                    formularyRules.filter { normalize(it.name) in dciForDx }
                } else {
                    formularyRules
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

        // =========================
        // 3) LISTA DE OPCIONES
        // =========================
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
                    Text(s.dci, fontWeight = FontWeight.Bold, color = title)
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

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = { selectedRx = s },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Usar esta receta", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        // =========================
        // 4) GUARDAR CONSULTA (ROOM)
        // =========================
        if (selectedRx != null && selectedDx != null) {
            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(14.dp)
            ) {
                Text("Receta seleccionada", fontWeight = FontWeight.Bold, color = title)
                Spacer(Modifier.height(6.dp))
                Text("DCI: ${selectedRx!!.dci}", color = title)
                Text("Dosis: ${selectedRx!!.dose}", color = title)
                Text("Frecuencia: ${selectedRx!!.frequency}", color = title)
                Text("Duración: ${selectedRx!!.duration}", color = title)
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas de la consulta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    val dx = selectedDx!!
                    val rx = selectedRx!!

                    vm.guardar(
                        pacienteId = paciente.id.toLong(),
                        pacienteNombre = paciente.nombres,
                        dxCie10 = dx.codigo,
                        dxDescripcion = dx.descripcion,
                        notas = notas.trim(),
                        rx = RxDraft(
                            dci = rx.dci,
                            forma = rx.form,
                            dosis = rx.dose,
                            frecuencia = rx.frequency,
                            duracion = rx.duration,
                            warnings = rx.warnings
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.isSaving
            ) {
                Text(
                    text = if (state.isSaving) "Guardando..." else "Guardar consulta",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.error!!, color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(18.dp))
    }
}

/* ======================
   DX RULES (assets json)
   ====================== */

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

        rules.firstOrNull { normalize(it.cie10) == codeN }?.let { return it }

        return rules.firstOrNull { rule ->
            rule.keywords.any { k -> descN.contains(normalize(k)) }
        }
    }
}

private fun normalize(s: String): String =
    s.trim().lowercase()
        .replace("á", "a").replace("é", "e").replace("í", "i")
        .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
