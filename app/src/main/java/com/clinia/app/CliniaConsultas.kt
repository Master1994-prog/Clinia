package com.clinia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CliniaConsultas() {
    val bg = Color(0xFFF4F7FB)
    val title = Color(0xFF0E1A2B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Text(
            text = "Consultas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = title
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("Historial de consultas (placeholder)", fontWeight = FontWeight.Bold, color = title)
            Spacer(Modifier.height(10.dp))
            Text("• 27/12 - PAC-001 - Control", color = title.copy(alpha = 0.75f))
            Text("• 26/12 - PAC-002 - Evaluación", color = title.copy(alpha = 0.75f))
            Text("• 25/12 - PAC-003 - Receta", color = title.copy(alpha = 0.75f))
            Spacer(Modifier.height(10.dp))
            Text(
                "Luego aquí conectamos con informes y prescripciones.",
                color = title.copy(alpha = 0.60f)
            )
        }
    }
}
