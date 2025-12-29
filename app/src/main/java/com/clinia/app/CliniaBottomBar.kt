package com.clinia.app

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlin.math.max
import kotlin.math.min

object CliniaTabRoutes {
    const val INICIO = "inicio"
    const val CHATBOX = "chatbox"
    const val PACIENTES = "pacientes"
    const val CONSULTAS = "consultas"
    const val AJUSTES = "ajustes"
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CliniaBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    height: Dp = 80.dp
) {
    val items = listOf(
        BottomItem(CliniaTabRoutes.INICIO, "Inicio", Icons.Filled.Home),
        BottomItem(CliniaTabRoutes.CHATBOX, "Chatbox", Icons.Filled.Chat),
        BottomItem(CliniaTabRoutes.PACIENTES, "Pacientes", Icons.Filled.Person),
        BottomItem(CliniaTabRoutes.CONSULTAS, "Consultas", Icons.Filled.List),
        BottomItem(CliniaTabRoutes.AJUSTES, "Ajustes", Icons.Filled.Settings),
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: CliniaTabRoutes.INICIO
    val selectedIndex = max(0, items.indexOfFirst { it.route == currentRoute })

    val t by animateFloatAsState(
        targetValue = selectedIndex / (items.size - 1f),
        animationSpec = spring(dampingRatio = 0.75f),
        label = "tab_anim"
    )

    val density = LocalDensity.current

    val bubbleRadius = 22.dp
    val cornerRadius = 26.dp
    val notchDepth = 18.dp

    val barBg = Color.White
    val iconInactive = Color(0xFF111827)
    val labelInactive = Color(0xFF111827)
    val bubbleColor = Color(0xFF2563EB) // azul
    val bubbleIcon = Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .height(height),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Barra notch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .shadow(10.dp, shape = RoundedCornerShape(28.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val br = with(density) { bubbleRadius.toPx() }
                val cr = with(density) { cornerRadius.toPx() }
                val nd = with(density) { notchDepth.toPx() }

                val left = cr
                val right = cr
                val cx = left + (w - left - right) * t

                drawCurvedBar(
                    barColor = barBg,
                    corner = cr,
                    bubbleR = br,
                    notchDepth = nd,
                    selectedX = cx,
                    width = w,
                    height = h
                )
            }
        }

        // Íconos + labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex

                Column(
                    modifier = Modifier
                        .width(66.dp)
                        .noRippleClickable {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(10.dp))

                    if (!selected) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconInactive,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        // espacio para que no “salte” el layout (porque el icono activo va en burbuja)
                        Spacer(Modifier.height(22.dp))
                    }

//                    Spacer(Modifier.height(4.dp))

//                    Text(
//                        text = item.label,
//                        fontSize = 11.sp,
//                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
//                        color = labelInactive
//                    )

                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        // Burbuja (icono activo)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            contentAlignment = Alignment.BottomStart
        ) {
            val sidePadding = 18.dp
            val usable = maxWidth - (sidePadding * 2)
            val centerX = sidePadding + usable * t
            val x = centerX - bubbleRadius
            val y = (-16).dp

            Box(
                modifier = Modifier
                    .offset(x = x, y = y)
                    .size(bubbleRadius * 2)
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) { drawCircle(color = bubbleColor) }
                Icon(
                    imageVector = items[selectedIndex].icon,
                    contentDescription = "Active",
                    tint = bubbleIcon,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawCurvedBar(
    barColor: Color,
    corner: Float,
    bubbleR: Float,
    notchDepth: Float,
    selectedX: Float,
    width: Float,
    height: Float
) {
    val notchWidth = bubbleR * 1.5f
    val start = selectedX - notchWidth
    val end = selectedX + notchWidth

    val p = Path()

    p.moveTo(corner, 0f)
    p.lineTo(max(corner, start), 0f)

    p.cubicTo(start, 0f, start, notchDepth, selectedX, notchDepth)
    p.cubicTo(end, notchDepth, end, 0f, min(width - corner, end), 0f)

    p.lineTo(width - corner, 0f)
    p.arcTo(Rect(width - 2 * corner, 0f, width, 2 * corner), -90f, 90f, false)
    p.lineTo(width, height - corner)
    p.arcTo(Rect(width - 2 * corner, height - 2 * corner, width, height), 0f, 90f, false)
    p.lineTo(corner, height)
    p.arcTo(Rect(0f, height - 2 * corner, 2 * corner, height), 90f, 90f, false)
    p.lineTo(0f, corner)
    p.arcTo(Rect(0f, 0f, 2 * corner, 2 * corner), 180f, 90f, false)

    p.close()
    drawPath(path = p, color = barColor, style = Fill)
}

@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
