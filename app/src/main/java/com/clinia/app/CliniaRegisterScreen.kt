package com.clinia.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CliniaRegisterScreen(
    onBackToLogin: () -> Unit,
    onRegisterDone: () -> Unit
) {
    // Colores CLINIA
    val bluePrimary = Color(0xFF2563EB)
    val blueTop = Color(0xFF4F8BFF)
    val blueMid = Color(0xFF7FB1FF)

    val textPrimary = Color(0xFF0E1A2B)
    val textSecondary = Color(0xFF6B7280)
    val fieldBorder = Color(0xFFD1D5DB)

    // Estado formulario
    var fullName by remember { mutableStateOf("") }
    var cmp by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }

    // Dropdown especialidad
    val specialties = listOf(
        "Medicina General",
        "Medicina Interna",
        "Pediatría",
        "Ginecología",
        "Cirugía",
        "Emergencias",
        "Otra"
    )
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Fondo degradado superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(blueTop, blueMid, Color.White),
                        startY = 0f,
                        endY = 1200f
                    )
                )
        )

        // ✅ LazyColumn = scroll real garantizado
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                // Volver
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "← Volver",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Robot
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.robot),
                        contentDescription = "Robot CLINIA",
                        modifier = Modifier.size(92.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Logo CLINIA tipográfico (imagen)
                Image(
                    painter = painterResource(id = R.drawable.clinia_robotico),
                    contentDescription = "CLINIA",
                    modifier = Modifier.width(190.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(18.dp))
            }

            item {
                // Tarjeta blanca
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    shape = RoundedCornerShape(26.dp),
                    color = Color.White,
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Registro de médico",
                            color = textPrimary,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Completa tus datos profesionales para acceder a CLINIA.",
                            color = textSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))

                        RoundedField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = "Nombre completo",
                            border = fieldBorder
                        )

                        Spacer(Modifier.height(12.dp))

                        RoundedField(
                            value = cmp,
                            onValueChange = { cmp = it },
                            placeholder = "Número de colegiatura (CMP)",
                            border = fieldBorder
                        )

                        Spacer(Modifier.height(12.dp))

                        // Especialidad dropdown
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = specialty,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Especialidad médica") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                shape = RoundedCornerShape(22.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = fieldBorder,
                                    unfocusedBorderColor = fieldBorder,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .height(56.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                specialties.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            specialty = item
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        RoundedField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Correo profesional",
                            border = fieldBorder
                        )

                        Spacer(Modifier.height(12.dp))

                        RoundedPasswordField(
                            value = pass,
                            onValueChange = { pass = it },
                            placeholder = "Contraseña",
                            border = fieldBorder
                        )

                        Spacer(Modifier.height(12.dp))

                        RoundedPasswordField(
                            value = pass2,
                            onValueChange = { pass2 = it },
                            placeholder = "Confirmar contraseña",
                            border = fieldBorder
                        )

                        Spacer(Modifier.height(12.dp))

                        if (error != null) {
                            Text(
                                text = error!!,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = {
                                error = null
                                if (fullName.isBlank() || cmp.isBlank() || specialty.isBlank()
                                    || email.isBlank() || pass.isBlank() || pass2.isBlank()
                                ) {
                                    error = "Completa todos los campos."
                                    return@Button
                                }
                                if (pass != pass2) {
                                    error = "Las contraseñas no coinciden."
                                    return@Button
                                }

                                // ✅ luego conectamos backend
                                onRegisterDone()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = bluePrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Registrarse", fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "¿Ya tienes cuenta? ",
                                color = textPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Iniciar sesión",
                                color = bluePrimary,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.clickable { onBackToLogin() }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Image(
                            painter = painterResource(id = R.drawable.lunaria),
                            contentDescription = "Lunaria",
                            modifier = Modifier.height(22.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            // ✅ espacio final para que el botón nunca quede pegado/cortado
            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoundedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    border: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = border,
            unfocusedBorderColor = border,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoundedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    border: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = border,
            unfocusedBorderColor = border,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}
