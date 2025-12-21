package com.clinia.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CliniaOnboardingSecond(onNext: () -> Unit) {

    val bluePrimary = Color(0xFF2563EB)
    val backgroundGray = Color(0xFFF4F5F7)
    val textPrimary = Color(0xFF0E1A2B)
    val textSecondary = Color(0xFF6B7280)
    val dotInactive = Color(0xFFD1D5DB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
    ) {

        // ───── Skip ─────
        Text(
            text = "Skip",
            style = MaterialTheme.typography.labelLarge,
            color = bluePrimary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 24.dp)
                .clickable { /* skip action */ }
        )

        // ───── Imagen grande ─────
        Image(
            painter = painterResource(id = R.drawable.doctor2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.68f)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 40.dp,
                        bottomEnd = 40.dp
                    )
                )
        )

        // ───── Contenedor inferior ─────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .offset(y = (-24).dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ───── Título ─────
                Text(
                    text = "Prevención de errores en la prescripción",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                // ───── Subtítulo ─────
                Text(
                    text = "CLINIA verifica interacciones y alertas clínicas en segundos, ayudando a evitar errores en la medicación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(22.dp))

                // ───── Dots (segundo activo) ─────
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Inactivo
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotInactive)
                    )

                    // Activo
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(8.dp)
                            .width(22.dp)
                            .clip(CircleShape)
                            .background(bluePrimary)
                    )
                }

                Spacer(Modifier.height(22.dp))

                // ───── Botón Next ─────
                Button(
                    onClick = { onNext() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = bluePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Next", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
