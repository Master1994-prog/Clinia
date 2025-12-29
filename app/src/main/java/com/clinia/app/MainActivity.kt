package com.clinia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clinia.app.data.local.CliniaDatabase
import com.clinia.app.data.local.DoctorEntity
import com.clinia.app.data.local.DoctorRepository
import com.clinia.app.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot() }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    val appContext = LocalContext.current.applicationContext
    val db = remember { CliniaDatabase.get(appContext) }
    val repo = remember { DoctorRepository(db.doctorDao()) }
    val vm = remember { AuthViewModel(repo) }

    val state by vm.state.collectAsState()
    val doctor = state.loggedDoctor

    // ✅ Doctor DEMO para probar login (Código: CLN-123 / Pass: 1234)
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

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            CliniaSplash(
                onFinished = { navController.navigate("onboarding1") }
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
                vm = vm,
                onLogin = {
                    // ✅ si el login fue OK, vm.state.loggedDoctor se llenará
                    navController.navigate("bienvenido") {
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
                        // ✅ evita volver a Bienvenido con back
                        popUpTo("bienvenido") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ✅✅ AQUÍ VA EXACTAMENTE LO QUE ME MOSTRASTE (MENU + LOGOUT REAL)
        composable("menu") {
            val nombreCompleto =
                doctor?.let { "${it.nombres} ${it.apellidos}" } ?: "Doctor(a)"

            CliniaMenuShell(
                doctorNombre = nombreCompleto,
                onLogout = {
                    vm.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // ✅ borra todo el backstack
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

// ✅ SHA256 para que la contraseña demo se guarde igual que el login (hash)
private fun String.sha256(): String {
    val md = java.security.MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
