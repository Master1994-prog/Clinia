package com.clinia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.clinia.app.data.local.CliniaDatabase
import com.clinia.app.data.local.DoctorEntity
import com.clinia.app.data.local.DoctorRepository
import com.clinia.app.ui.auth.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot() }
    }
}

enum class Screen {
    Splash,
    Onboarding1,
    Onboarding2,
    Login,
    Register,
    Bienvenido
}

@Composable
fun AppRoot() {

    var currentScreen by remember { mutableStateOf(Screen.Splash) }

    val appContext = LocalContext.current.applicationContext
    val db = remember { CliniaDatabase.get(appContext) }
    val repo = remember { DoctorRepository(db.doctorDao()) }
    val vm = remember { AuthViewModel(repo) }

    // ✅ Estado del VM (para leer loggedDoctor)
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

    when (currentScreen) {

        Screen.Splash -> CliniaSplash(
            onFinished = { currentScreen = Screen.Onboarding1 }
        )

        Screen.Onboarding1 -> CliniaOnboardingScreen(
            onNext = { currentScreen = Screen.Onboarding2 }
        )

        Screen.Onboarding2 -> CliniaOnboardingSecond(
            onNext = { currentScreen = Screen.Login }
        )

        Screen.Login -> CliniaLogin(
            vm = vm,
            onLogin = { currentScreen = Screen.Bienvenido },
            onRegister = { currentScreen = Screen.Register }
        )

        Screen.Bienvenido -> {
            val nombreCompleto = doctor?.let { "${it.nombres} ${it.apellidos}" } ?: "Doctor(a)"
            CliniaBienvenido(
                doctorNombre = nombreCompleto,
                onContinue = {
                    // TODO: aquí luego tu Dashboard real
                    currentScreen = Screen.Onboarding1
                }
            )
        }

        Screen.Register -> CliniaRegisterScreen(
            onBackToLogin = { currentScreen = Screen.Login },
            onRegisterDone = { currentScreen = Screen.Login }
        )
    }
}

// ✅ SHA256 para que la contraseña demo se guarde igual que el login (hash)
private fun String.sha256(): String {
    val md = java.security.MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
