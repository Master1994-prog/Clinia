package com.clinia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CliniaAjustes(
    onLogout: () -> Unit
) {
    val bg = Color(0xFFF4F7FB)
    val title = Color(0xFF0E1A2B)

    var notificaciones by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = title
        )

        Spacer(Modifier.height(12.dp))

        // Caja de contenidos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("Preferencias", fontWeight = FontWeight.Bold, color = title)
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Notificaciones: ${if (notificaciones) "Activadas" else "Desactivadas"}",
                color = title.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { notificaciones = !notificaciones },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88A8)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cambiar notificaciones", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ✅ Logout directo
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)), // azul
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Cerrar sesión",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
