package com.tecsup.lab13animaciones.ejercicios

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class EstadoPantalla {
    CARGANDO,
    CONTENIDO,
    ERROR
}

@Composable
fun Ejercicio4Screen() {
    var estadoActual by remember { mutableStateOf(EstadoPantalla.CARGANDO) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ejercicio 4: AnimatedContent",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = estadoActual,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(durationMillis = 700)) togetherWith
                                fadeOut(animationSpec = tween(durationMillis = 400))
                    },
                    label = "StateTransition"
                ) { estado ->
                    when (estado) {
                        EstadoPantalla.CARGANDO -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Cargando datos del servidor...")
                            }
                        }
                        EstadoPantalla.CONTENIDO -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Datos cargados correctamente",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Bienvenido al panel principal.")
                            }
                        }
                        EstadoPantalla.ERROR -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Error de conexión",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFFC62828)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No se pudo obtener la información. Intenta de nuevo.")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { estadoActual = EstadoPantalla.CARGANDO }) {
                Text("Cargando")
            }
            Button(onClick = { estadoActual = EstadoPantalla.CONTENIDO }) {
                Text("Contenido")
            }
            Button(onClick = { estadoActual = EstadoPantalla.ERROR }) {
                Text("Error")
            }
        }
    }
}