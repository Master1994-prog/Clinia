package com.clinia.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clinia.app.ui.auth.AuthViewModel

@Composable
fun CliniaLogin(
    vm: AuthViewModel,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    // 🎨 Colores (tu estilo)
    val bluePrimary = Color(0xFF2563EB)
    val textPrimary = Color(0xFF0E1A2B)
    val fieldBorder = Color(0xFFD1D5DB)

    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by vm.state.collectAsState()

    // ✅ cuando el login sea correcto, navega
    LaunchedEffect(state.loggedDoctor) {
        if (state.loggedDoctor != null) onLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Fondo degradado superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4F8BFF),
                            Color(0x802563EB),
                            Color(0x802563EB),
                            Color(0x00FFFFFF)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Círculo blanco con robot
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(90.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.robot),
                    contentDescription = "Logo Clinia",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(Modifier.height(30.dp))

            // Logo CLINIA
            Image(
                painter = painterResource(id = R.drawable.clinia_robotico),
                contentDescription = "Logo tipográfico CLINIA",
                modifier = Modifier
                    .width(180.dp)
                    .padding(top = 8.dp)
            )

            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Introduce el número de colegiatura.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it; vm.clearMessages() },
                        placeholder = { Text("Código de acceso") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = state.error != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = fieldBorder,
                            unfocusedBorderColor = fieldBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; vm.clearMessages() },
                        placeholder = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = state.error != null,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = fieldBorder,
                            unfocusedBorderColor = fieldBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // ✅ mensaje de error
                    state.error?.let {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = it,
                            color = Color(0xFFDC2626),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { vm.login(code, password) },
                        enabled = !state.loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = bluePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (state.loading) "Validando..." else "Ingresar",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Eres médico nuevo? ",
                            color = textPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = onRegister) {
                            Text(
                                text = "Registrarse",
                                color = bluePrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    Image(
                        painter = painterResource(id = R.drawable.lunaria),
                        contentDescription = "Logo Lunaria",
                        modifier = Modifier
                            .height(40.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 20.dp)
                    )
                }
            }
        }
    }
}
