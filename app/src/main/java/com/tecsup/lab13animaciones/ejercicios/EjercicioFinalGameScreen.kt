package com.tecsup.lab13animaciones.ejercicios
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class EstadoJuego { COMBATE, VICTORIA, DERROTA }

@Composable
fun EjercicioFinalGameScreen() {
    val scope = rememberCoroutineScope()

    var estadoJuego by remember { mutableStateOf(EstadoJuego.COMBATE) }
    var vidaHeroe by remember { mutableIntStateOf(100) }
    var vidaJefe by remember { mutableIntStateOf(100) }
    var energiaUlti by remember { mutableIntStateOf(0) } // 0 a 100

    var turnoEnCurso by remember { mutableStateOf(false) }
    var heroeAtacando by remember { mutableStateOf(false) }
    var ultiActiva by remember { mutableStateOf(false) }
    var jefeAtacando by remember { mutableStateOf(false) }
    var escudoActivo by remember { mutableStateOf(false) }

    var danioAlJefe by remember { mutableStateOf<String?>(null) }
    var danioAlHeroe by remember { mutableStateOf<String?>(null) }
    var mensaje by remember { mutableStateOf("Inicia el combate contra el Jefe") }

    // animateColorAsState: Color de la arena y del Jefe
    val colorArena by animateColorAsState(
        targetValue = when {
            ultiActiva -> Color(0xFF5D4037)      // Dorado oscuro al lanzar Ulti
            heroeAtacando -> Color(0xFF4A148C)   // Morado al atacar
            jefeAtacando -> Color(0xFF7F0000)    // Rojo al contraataque del Jefe
            escudoActivo -> Color(0xFF0D47A1)    // Azul con escudo
            else -> Color(0xFF1E1E2C)
        },
        animationSpec = tween(300),
        label = "ColorArena"
    )

    val colorJefe by animateColorAsState(
        targetValue = if (vidaJefe > 40) Color(0xFFE53935) else Color(0xFF8E24AA),
        animationSpec = tween(500),
        label = "ColorJefe"
    )

    //animateDpAsState + offset/size: Movimiento y tamaño de personajes
    val heroeOffsetX by animateDpAsState(
        targetValue = when {
            heroeAtacando || ultiActiva -> 75.dp  // Avanza para atacar
            jefeAtacando -> (-18).dp              // Retrocede al recibir golpe
            else -> 0.dp
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "HeroeOffset"
    )

    val jefeOffsetX by animateDpAsState(
        targetValue = when {
            jefeAtacando -> (-75).dp              // Avanza hacia el Héroe
            heroeAtacando || ultiActiva -> 20.dp  // Retrocede al recibir daño
            else -> 0.dp
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "JefeOffset"
    )

    val jefeTamanio by animateDpAsState(
        targetValue = if (vidaJefe <= 40) 110.dp else 85.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "JefeTamanio"
    )

    fun reiniciar() {
        vidaHeroe = 100
        vidaJefe = 100
        energiaUlti = 0
        turnoEnCurso = false
        heroeAtacando = false
        ultiActiva = false
        jefeAtacando = false
        escudoActivo = false
        danioAlJefe = null
        danioAlHeroe = null
        mensaje = "Nuevo combate iniciado"
        estadoJuego = EstadoJuego.COMBATE
    }

    // Secuencia de contraataque del Jefe
    fun contraataqueJefe() {
        scope.launch {
            delay(350)
            jefeAtacando = true
            val danio = when {
                escudoActivo -> 8            // El escudo bloquea la mayor parte del daño
                vidaJefe <= 40 -> 32         // Daño letal en modo FURIA
                else -> 25                   // Daño normal aumentado
            }
            vidaHeroe = (vidaHeroe - danio).coerceAtLeast(0)
            danioAlHeroe = if (escudoActivo) "-$danio (Bloqueado)" else "-$danio HP"
            mensaje = "El Jefe contraataca (-$danio HP)"
            escudoActivo = false

            delay(400)
            jefeAtacando = false
            delay(250)
            danioAlHeroe = null

            if (vidaHeroe == 0) estadoJuego = EstadoJuego.DERROTA
            turnoEnCurso = false
        }
    }

    fun atacar(esUlti: Boolean = false) {
        if (turnoEnCurso || estadoJuego != EstadoJuego.COMBATE) return
        turnoEnCurso = true

        scope.launch {
            val danio = if (esUlti) 40 else 20
            if (esUlti) {
                energiaUlti = 0
                ultiActiva = true
                danioAlJefe = "-40 ULTI!"
                mensaje = "Lanzaste tu ULTIMATE (-40 HP)"
            } else {
                heroeAtacando = true
                energiaUlti = (energiaUlti + 35).coerceAtMost(100)
                danioAlJefe = "-20 HP"
                mensaje = "Atacaste al Jefe (-20 HP)"
            }

            vidaJefe = (vidaJefe - danio).coerceAtLeast(0)
            delay(400)
            heroeAtacando = false
            ultiActiva = false
            delay(250)
            danioAlJefe = null

            if (vidaJefe == 0) {
                estadoJuego = EstadoJuego.VICTORIA
                turnoEnCurso = false
            } else {
                contraataqueJefe()
            }
        }
    }

    fun defender() {
        if (turnoEnCurso || estadoJuego != EstadoJuego.COMBATE) return
        turnoEnCurso = true
        escudoActivo = true
        vidaHeroe = (vidaHeroe + 15).coerceAtMost(100)
        energiaUlti = (energiaUlti + 30).coerceAtMost(100)
        mensaje = "Escudo activo (+15 HP, +30% Ulti)"
        contraataqueJefe()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .systemBarsPadding() // Empuja el contenido debajo de la cámara/hora y encima de la barra inferior
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PROTOTIPO DE COMBATE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // AnimatedContent Transición entre COMBATE, VICTORIA y DERROTA
        AnimatedContent(
            targetState = estadoJuego,
            transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(400)) },
            label = "EstadoJuego",
            modifier = Modifier.weight(1f)
        ) { estado ->
            when (estado) {
                EstadoJuego.COMBATE -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Barras de vida y carga de Ulti
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Héroe: $vidaHeroe HP", color = Color(0xFF4CAF50))
                                    LinearProgressIndicator(
                                        progress = { vidaHeroe / 100f },
                                        modifier = Modifier.fillMaxWidth().height(8.dp),
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Jefe: $vidaJefe HP", color = colorJefe)
                                    LinearProgressIndicator(
                                        progress = { vidaJefe / 100f },
                                        modifier = Modifier.fillMaxWidth().height(8.dp),
                                        color = colorJefe
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Carga Ulti: $energiaUlti%", color = Color(0xFFFFD54F), fontSize = 12.sp)
                            LinearProgressIndicator(
                                progress = { energiaUlti / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = Color(0xFFFFD54F)
                            )
                        }

                        // Arena de combate
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorArena)
                                .padding(20.dp)
                        ) {
                            // Personaje Héroe
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .offset(x = heroeOffsetX),
                                contentAlignment = Alignment.Center
                            ) {
                                //  Aura del escudo
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = escudoActivo,
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut() + scaleOut()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(92.dp)
                                            .border(4.dp, Color(0xFF00E5FF), CircleShape)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .background(if (ultiActiva) Color(0xFFFFB300) else Color(0xFF1E88E5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("HEROE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            // Personaje Jefe
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .offset(x = jefeOffsetX)
                                    .size(jefeTamanio)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colorJefe),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (vidaJefe <= 40) "JEFE\nFURIA" else "JEFE",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            //  Daño sobre el Jefe
                            androidx.compose.animation.AnimatedVisibility(
                                visible = danioAlJefe != null,
                                enter = slideInVertically { it / 2 } + fadeIn(),
                                exit = slideOutVertically { -it } + fadeOut(),
                                modifier = Modifier.align(Alignment.TopEnd).padding(end = 12.dp, top = 8.dp)
                            ) {
                                Text(
                                    text = danioAlJefe.orEmpty(),
                                    color = Color.Yellow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }

                            // AnimatedVisibility: Daño sobre el Héroe
                            androidx.compose.animation.AnimatedVisibility(
                                visible = danioAlHeroe != null,
                                enter = slideInVertically { it / 2 } + fadeIn(),
                                exit = slideOutVertically { -it } + fadeOut(),
                                modifier = Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 8.dp)
                            ) {
                                Text(
                                    text = danioAlHeroe.orEmpty(),
                                    color = Color(0xFFFF5252),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Text(text = mensaje, color = Color.LightGray)

                        // Controles del jugador
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { atacar(esUlti = false) },
                                    enabled = !turnoEnCurso,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                                ) {
                                    Text("Atacar")
                                }
                                Button(
                                    onClick = { defender() },
                                    enabled = !turnoEnCurso,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                                ) {
                                    Text("Escudo")
                                }
                            }

                            // AnimatedVisibility: Botón Ulti al 100%
                            androidx.compose.animation.AnimatedVisibility(
                                visible = energiaUlti >= 100,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                Button(
                                    onClick = { atacar(esUlti = true) },
                                    enabled = !turnoEnCurso,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFB300),
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text("LANZAR ULTI (-40 HP)", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                EstadoJuego.VICTORIA -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("VICTORIA", color = Color(0xFF4CAF50), fontSize = 34.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { reiniciar() }) { Text("Jugar de nuevo") }
                    }
                }

                EstadoJuego.DERROTA -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("GAME OVER", color = Color(0xFFE53935), fontSize = 34.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { reiniciar() }) { Text("Reintentar") }
                    }
                }
            }
        }
    }
}