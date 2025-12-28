package com.clinia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
    var showLogoutDialog by remember { mutableStateOf(false) }

    // 🔹 CONTENEDOR GENERAL (permite blur + scrim)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {

        // 🔹 CONTENIDO PRINCIPAL (se difumina)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (showLogoutDialog) 14.dp else 0.dp) // ✅ BLUR
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // TÍTULO
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = title
            )

            Spacer(Modifier.height(12.dp))

            // CAJA DE CONTENIDOS
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88A8))
                ) {
                    Text("Cambiar notificaciones", color = Color.White)
                }

                // 👉 aquí puedes seguir agregando opciones
            }

            Spacer(Modifier.height(16.dp))

            // BOTÓN DEBAJO DE LA CAJA
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
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

        // 🔹 SCRIM OSCURO (mejora el blur)
        if (showLogoutDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )
        }

        // 🔹 ALERT DIALOG SÓLIDO (SIN DEGRADADO)
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = Color(0xFFF1F5F9), // ✅ FONDO SÓLIDO (plomo claro)
                shape = RoundedCornerShape(20.dp),

                title = {
                    Text(
                        text = "Cerrar sesión",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0E1A2B)
                    )
                },
                text = {
                    Text(
                        text = "¿Seguro que deseas cerrar sesión?",
                        color = Color(0xFF334155)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB), // 🔵 azul hex
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showLogoutDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF94A3B8),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
