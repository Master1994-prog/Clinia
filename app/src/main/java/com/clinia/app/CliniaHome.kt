package com.clinia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CliniaHome(doctorNombre: String) {
    val bg = Color(0xFFF4F7FB)
    val title = Color(0xFF0E1A2B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Text(
            text = "Inicio",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = title
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Hola, Dr(a). $doctorNombre",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = title
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(title = "Pacientes", value = "12", emoji = "👥", modifier = Modifier.weight(1f))
            InfoCard(title = "Consultas", value = "5", emoji = "📋", modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(title = "Chatbox", value = "Listo", emoji = "🤖", modifier = Modifier.weight(1f))
            InfoCard(title = "Alertas", value = "2", emoji = "🔔", modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // ✅ esto está dentro de Column, es válido
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Panel principal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = title
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Aquí irá tu resumen clínico, accesos rápidos y el asistente para prescripción.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = title.copy(alpha = 0.75f)
                )
                Spacer(Modifier.height(14.dp))

                FeatureLine("✅", "Validación de acceso activa")
                FeatureLine("🔒", "Privacidad y seguridad")
                FeatureLine("💊", "Soporte para prescripción")
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    val titleColor = Color(0xFF0E1A2B)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Column {
            Text(text = emoji, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.SemiBold, color = titleColor)
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge,
                color = titleColor
            )
        }
    }
}

@Composable
private fun FeatureLine(
    emoji: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(text = emoji)
        Spacer(Modifier.width(10.dp))
        Text(text, color = Color(0xFF0E1A2B), fontWeight = FontWeight.Medium)
    }
}
