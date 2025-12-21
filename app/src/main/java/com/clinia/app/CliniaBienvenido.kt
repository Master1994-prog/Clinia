package com.clinia.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CliniaBienvenido(
    doctorNombre: String,          // ← nombre REAL del doctor
    onContinue: () -> Unit
) {
    // 🎨 Colores estilo mockup
    val skyTop = Color(0xFF78B6E7)
    val skyMid = Color(0xFF8FC3ED)
    val skyBottom = Color(0xFFCFE9F8)

    val pinkBadge = Color(0xFFF2C8D8)
    val textPrimary = Color(0xFF0E1A2B)

    // 🤖 Frames del robot (tus imágenes)
    val frameDown = R.drawable.robot_0
    val frameUpA = R.drawable.robot_1
    val frameUpB = R.drawable.robot_2

    // 🎞 Animación por frames (saludo)
    var frameIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            frameIndex = 0
            delay(500)

            repeat(6) {
                frameIndex = 1
                delay(200)
                frameIndex = 2
                delay(200)
            }

            frameIndex = 0
            delay(700)
        }
    }

    val currentFrame = when (frameIndex) {
        0 -> frameDown
        1 -> frameUpA
        else -> frameUpB
    }

    // 🌈 Layout principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(skyTop, skyMid, skyBottom))
            )
            .padding(horizontal = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🌸 Badge
            Box(
                modifier = Modifier
                    .background(pinkBadge, RoundedCornerShape(999.dp))
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Acceso verificado",
                    color = textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(18.dp))

            // 🏷️ Título
            Text(
                text = "Bienvenido a\nCLINIA",
                color = textPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(10.dp))

            // 👨‍⚕️ Nombre del doctor (REAL)
            Text(
                text = "Dr(a). $doctorNombre",
                color = textPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(14.dp))

            // 📝 Mensaje
            Text(
                text = "Tu acceso fue validado correctamente.\nYa puedes continuar con la aplicación.",
                color = textPrimary.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.weight(1f))

            // 🤖 Robot animado
            Image(
                painter = painterResource(id = currentFrame),
                contentDescription = "Robot Clinia",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            )

            Spacer(Modifier.height(12.dp))

            // 👉 Texto de continuar (puede ser botón luego)
            Text(
                text = "Continuar",
                color = textPrimary.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
