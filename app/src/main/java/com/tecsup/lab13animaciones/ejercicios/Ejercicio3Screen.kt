package com.tecsup.lab13animaciones.ejercicios

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Ejercicio3Screen() {
    var expandidoYMovido by remember { mutableStateOf(false) }
    var offsetPrimero by remember { mutableStateOf(true) }

    val tamanio by animateDpAsState(
        targetValue = if (expandidoYMovido) 150.dp else 80.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "SizeAnimation"
    )

    val desplazamientoX by animateDpAsState(
        targetValue = if (expandidoYMovido) 60.dp else (-60).dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "OffsetXAnimation"
    )

    val desplazamientoY by animateDpAsState(
        targetValue = if (expandidoYMovido) 40.dp else (-20).dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "OffsetYAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ejercicio 3: Tamaño y Posición",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (offsetPrimero) "Orden: offset -> size -> background"
            else "Orden: background -> offset -> size (el fondo NO se mueve)",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            val boxModifier = if (offsetPrimero) {
                Modifier
                    .offset(x = desplazamientoX, y = desplazamientoY)
                    .size(tamanio)
                    .background(Color(0xFFFF7043), RoundedCornerShape(12.dp))
            } else {
                Modifier
                    .background(Color(0x33FF7043), RoundedCornerShape(12.dp))
                    .offset(x = desplazamientoX, y = desplazamientoY)
                    .size(tamanio)
                    .border(2.dp, Color(0xFFFF7043), RoundedCornerShape(12.dp))
            }

            Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
                Text(text = "${tamanio.value.toInt()}dp", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { expandidoYMovido = !expandidoYMovido }) {
            Text(if (expandidoYMovido) "Restaurar posición y tamaño" else "Mover y agrandar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = { offsetPrimero = !offsetPrimero }) {
            Text("Alternar orden de Modifiers")
        }
    }
}