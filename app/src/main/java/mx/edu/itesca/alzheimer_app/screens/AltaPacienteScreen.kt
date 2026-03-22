package mx.edu.itesca.alzheimer_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.itesca.alzheimer_app.database.DatabaseHelper
import mx.edu.itesca.alzheimer_app.model.Paciente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AltaPacienteScreen(
    db: DatabaseHelper,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var generoSeleccionado by remember { mutableStateOf("Masculino") }
    var expandedGenero by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf("") }

    val generos = listOf("Masculino", "Femenino")

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Nuevo Paciente",
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                label = { Text("Fecha de nacimiento (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Dropdown genero
            ExposedDropdownMenuBox(
                expanded = expandedGenero,
                onExpandedChange = { expandedGenero = it }
            ) {
                OutlinedTextField(
                    value = generoSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenero)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                )
                ExposedDropdownMenu(
                    expanded = expandedGenero,
                    onDismissRequest = { expandedGenero = false }
                ) {
                    generos.forEach { genero ->
                        DropdownMenuItem(
                            text = { Text(genero) },
                            onClick = {
                                generoSeleccionado = genero
                                expandedGenero = false
                            }
                        )
                    }
                }
            }

            if (errorMensaje.isNotEmpty()) {
                Text(
                    text = errorMensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || apellido.isBlank() || fechaNacimiento.isBlank()) {
                        errorMensaje = "Por favor completa todos los campos obligatorios"
                    } else {
                        db.insertarPaciente(
                            Paciente(
                                nombre = nombre.trim(),
                                apellido = apellido.trim(),
                                fechaNacimiento = fechaNacimiento.trim(),
                                genero = generoSeleccionado,
                                telefono = telefono.trim()
                            )
                        )
                        onGuardar()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar paciente")
            }

            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}