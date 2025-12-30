package com.clinia.app.pacientes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PacienteCard(
    p: Paciente,
    onSelect: (Paciente) -> Unit = {}
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
        Text(
            "Edad: ${p.edad} años • Sexo: ${p.sexo}",
            color = title.copy(alpha = 0.75f)
        )
        Text(
            "Peso: ${p.peso ?: "--"} kg • Talla: ${p.talla ?: "--"} cm",
            color = title.copy(alpha = 0.75f)
        )

        if (p.ram.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                "RAM: ${p.ram.joinToString()}",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
