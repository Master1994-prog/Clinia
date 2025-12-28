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
fun CliniaPacientes() {
    val bg = Color(0xFFF4F7FB)
    val title = Color(0xFF0E1A2B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Text(
            text = "Pacientes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = title
        )

        Spacer(Modifier.height(12.dp))

        // Placeholder lista
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("Listado de pacientes (placeholder)", fontWeight = FontWeight.Bold, color = title)
            Spacer(Modifier.height(10.dp))
            Text("• PAC-001  — Juan Pérez", color = title.copy(alpha = 0.75f))
            Text("• PAC-002  — María Díaz", color = title.copy(alpha = 0.75f))
            Text("• PAC-003  — Luis García", color = title.copy(alpha = 0.75f))
            Spacer(Modifier.height(10.dp))
            Text(
                "Luego aquí conectamos con base de datos y búsqueda.",
                color = title.copy(alpha = 0.60f)
            )
        }
    }
}
