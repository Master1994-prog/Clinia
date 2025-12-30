package com.clinia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.clinia.app.pacientes.Paciente
import com.clinia.app.pacientes.PacienteStore

@Composable
fun CliniaPacientes(navController: NavHostController) {

    val bg = Color(0xFFF4F7FB)
    val title = Color(0xFF0E1A2B)

    // DEMO: luego lo sacas de Room
    val pacientes = remember {
        listOf(
            Paciente(
                id = 1,
                nombres = "María López",
                edad = 32,
                sexo = "F",
                peso = 62.0,
                talla = 160.0,
                embarazada = false,
                ram = listOf("alergia_penicilina")
            ),
            Paciente(
                id = 2,
                nombres = "Juan Pérez",
                edad = 45,
                sexo = "M",
                peso = 78.0,
                talla = 170.0,
                embarazada = false,
                ram = emptyList()
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Pacientes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = title
        )

        Spacer(Modifier.height(12.dp))

        pacientes.forEach { p ->
            PacienteCard(
                p = p,
                onSelect = {
                    // ✅ 1) Guardar paciente activo
                    PacienteStore.pacienteActivo = it

                    // ✅ 2) Ir al chatbot directamente
                    navController.navigate(CliniaTabRoutes.CHATBOX) {
                        launchSingleTop = true
                    }
                }
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun PacienteCard(
    p: Paciente,
    onSelect: (Paciente) -> Unit
) {
    val title = Color(0xFF0E1A2B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
            .clickable { onSelect(p) }
    ) {
        Text(p.nombres, fontWeight = FontWeight.Bold, color = title)
        Spacer(Modifier.height(4.dp))
        Text("Edad: ${p.edad} • Sexo: ${p.sexo}", color = title.copy(alpha = 0.75f))
        Text("Peso: ${p.peso ?: "--"} kg • Talla: ${p.talla ?: "--"} cm", color = title.copy(alpha = 0.75f))

        if (p.ram.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("RAM: ${p.ram.joinToString()}", color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
        }
    }
}
