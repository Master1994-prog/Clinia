package com.clinia.app

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clinia.app.data.local.CliniaDatabase
import com.clinia.app.data.local.DoctorEntity
import com.clinia.app.data.local.DoctorRepository
import com.clinia.app.ui.auth.AuthViewModel
import java.security.MessageDigest

@Composable
fun AppRoot() {

    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext

    // ===============================
    // BASE DE DATOS + REPOSITORIO
    // ===============================
    val db = remember { CliniaDatabase.get(context) }
    val repo = remember { DoctorRepository(db.doctorDao()) }

    // ===============================
    // VIEWMODEL (sin Hilt por ahora)
    // ===============================
    val authVm = remember { AuthViewModel(repo) }
    val authState by authVm.state.collectAsState()
    val doctor = authState.loggedDoctor

    // ===============================
    // DOCTOR DEMO (solo para pruebas)
    // ===============================
    LaunchedEffect(Unit) {
        runCatching {
            repo.registerDoctor(
                DoctorEntity(
                    nombres = "Roger",
                    apellidos = "Espinoza",
                    cmp = "999999",
                    codigo = "CLN-123",
                    passwordHash = "1234".sha256(),
                    activo = true
                )
            )
        }
    }

    // ===============================
    // NAVEGACIÓN PRINCIPAL
    // ===============================
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            CliniaSplash(
                onFinished = {
                    navController.navigate("onboarding1") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding1") {
            CliniaOnboardingScreen(
                onNext = { navController.navigate("onboarding2") }
            )
        }

        composable("onboarding2") {
            CliniaOnboardingSecond(
                onNext = { navController.navigate("login") }
            )
        }

        composable("login") {
            CliniaLogin(
                vm = authVm,
                onLogin = {
                    navController.navigate("bienvenido") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            CliniaRegisterScreen(
                onBackToLogin = { navController.popBackStack() },
                onRegisterDone = { navController.popBackStack() }
            )
        }

        composable("bienvenido") {
            val nombreCompleto =
                doctor?.let { "${it.nombres} ${it.apellidos}" } ?: "Doctor(a)"

            CliniaBienvenido(
                doctorNombre = nombreCompleto,
                onContinue = {
                    navController.navigate("menu") {
                        popUpTo("bienvenido") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("menu") {
            val nombreCompleto =
                doctor?.let { "${it.nombres} ${it.apellidos}" } ?: "Doctor(a)"

            CliniaMenuShell(
                doctorNombre = nombreCompleto,
                medicoId = doctor?.id ?: 0L,
                onLogout = {
                    authVm.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

/**
 * HASH SHA-256
 * (para que el password demo coincida con el login)
 */
private fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
