package mx.edu.itesca.alzheimer_app.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.itesca.alzheimer_app.database.DatabaseHelper
import mx.edu.itesca.alzheimer_app.model.Evaluacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgresoScreen(
    db: DatabaseHelper,
    pacienteId: Int,
    onRegresar: () -> Unit
) {
    val evaluaciones = remember { db.obtenerEvaluacionesPorPaciente(pacienteId) }
    val mmse = evaluaciones.filter { it.instrumento == "MMSE" }.reversed()
    val tinetti = evaluaciones.filter { it.instrumento == "Tinetti" }.reversed()

    var tabSeleccionado by remember { mutableIntStateOf(0) }
    val tabs = listOf("MMSE", "Tinetti")

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Progreso del Paciente",
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = tabSeleccionado) {
                tabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionado == index,
                        onClick = { tabSeleccionado = index },
                        text = { Text(titulo) }
                    )
                }
            }

            val datos = if (tabSeleccionado == 0) mmse else tinetti
            val puntajeMax = if (tabSeleccionado == 0) 30 else 28

            if (datos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay evaluaciones de ${tabs[tabSeleccionado]}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Evolución de puntajes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GraficaProgreso(
                            evaluaciones = datos,
                            puntajeMaximo = puntajeMax,
                            color = if (tabSeleccionado == 0)
                                Color(0xFF6650A4) else Color(0xFF0B6E4F)
                        )
                    }

                    item {
                        Text(
                            text = "Detalle de evaluaciones",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    items(datos.size) { index ->
                        val evaluacion = datos[index]
                        CardEvaluacion(evaluacion = evaluacion)
                    }
                }
            }
        }
    }
}

@Composable
fun GraficaProgreso(
    evaluaciones: List<Evaluacion>,
    puntajeMaximo: Int,
    color: Color
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (evaluaciones.size == 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = evaluaciones[0].fecha,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${evaluaciones[0].puntaje} / $puntajeMaximo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registra más evaluaciones para ver la gráfica de progreso",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val ancho = size.width
                    val alto = size.height
                    val pasoX = ancho / (evaluaciones.size - 1).toFloat()
                    val puntos = evaluaciones.mapIndexed { i, eval ->
                        Offset(
                            x = i * pasoX,
                            y = alto - (eval.puntaje.toFloat() / puntajeMaximo) * alto
                        )
                    }

                    // Linea de referencia maxima
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, 0f),
                        end = Offset(ancho, 0f),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Path de la grafica
                    val path = Path().apply {
                        moveTo(puntos[0].x, puntos[0].y)
                        for (i in 1 until puntos.size) {
                            lineTo(puntos[i].x, puntos[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Puntos
                    puntos.forEach { punto ->
                        drawCircle(
                            color = color,
                            radius = 5.dp.toPx(),
                            center = punto
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = punto
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fechas debajo de la grafica
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = evaluaciones.first().fecha,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = evaluaciones.last().fecha,
                        fontSize = 11.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}