package com.tecsup.lab13animaciones.ejercicios

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Ejercicio2Screen() {
    var esAzul by remember { mutableStateOf(true) }
    var usarSpring by remember { mutableStateOf(false) }

    val colorAnimado by animateColorAsState(
        targetValue = if (esAzul) Color(0xFF1976D2) else Color(0xFF388E3C),
        animationSpec = if (usarSpring) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        } else {
            tween(durationMillis = 800)
        },
        label = "ColorBoxAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ejercicio 2: animateColorAsState",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Modo actual: ${if (usarSpring) "Spring (rebote físico)" else "Tween (800ms lineal/curva)"}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colorAnimado),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (esAzul) "Azul" else "Verde",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { esAzul = !esAzul }) {
            Text("Cambiar Color")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = { usarSpring = !usarSpring }) {
            Text(if (usarSpring) "Cambiar a Tween" else "Cambiar a Spring")
        }
    }
}