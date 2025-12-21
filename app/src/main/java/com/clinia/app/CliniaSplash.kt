package com.clinia.app

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CliniaSplash(
    onFinished: () -> Unit   // 👈 ESTE PARÁMETRO ES OBLIGATORIO
) {
    var startAnimation by remember { mutableStateOf(false) }
    var shrinkRobot by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }

    val bounceEasing = Easing { OvershootInterpolator(1.6f).getInterpolation(it) }

    val robotOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 120.dp,
        animationSpec = tween(
            durationMillis = 900,
            easing = bounceEasing
        ),
        label = "robotOffsetY"
    )

    val robotScale by animateFloatAsState(
        targetValue = if (shrinkRobot) 0.85f else 1f,
        animationSpec = tween(
            durationMillis = 350,
            easing = { it }
        ),
        label = "robotScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(900)
        shrinkRobot = true
        delay(300)
        showLogo = true
        delay(1500)
        onFinished()         // 👈 AQUÍ SE LLAMA CUANDO ACABA
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.robot),
                contentDescription = "Robot Clinia",
                modifier = Modifier
                    .offset(y = robotOffsetY)
                    .size(200.dp)
                    .graphicsLayer(
                        scaleX = robotScale,
                        scaleY = robotScale
                    )
            )

            Spacer(modifier = Modifier.height(6.dp))

            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 500)
                ) + slideInHorizontally(
                    initialOffsetX = { it }
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_clinia),
                    contentDescription = "Logo Clinia",
                    modifier = Modifier.height(60.dp)
                )
            }
        }
    }
}
