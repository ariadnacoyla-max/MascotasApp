
package com.tecsup.mascotasapp.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.tecsup.mascotasapp.data.MascotaModel
import com.tecsup.mascotasapp.data.RetrofitClient
import kotlinx.coroutines.launch

// Pantalla principal para gestionar mascotas con Django
@Composable
fun ScreenMascotas() {

    val scope = rememberCoroutineScope()

    // Lista obtenida desde la API REST
    var mascotas by remember {
        mutableStateOf<List<MascotaModel>>(emptyList())
    }

    var cargando by remember { mutableStateOf(true) }
    var procesando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    // Control del formulario
    var mostrarFormulario by remember { mutableStateOf(false) }
    var idEditando by remember { mutableStateOf<Int?>(null) }

    // Datos del formulario
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }

    // Mascota seleccionada para eliminar
    var mascotaEliminar by remember {
        mutableStateOf<MascotaModel?>(null)
    }

    // Limpia los campos del formulario
    fun limpiarFormulario() {
        nombre = ""
        especie = ""
        raza = ""
        edad = ""
        idEditando = null
    }

    // GET: Consulta las mascotas en Django
    suspend fun cargarMascotas() {
        mascotas = RetrofitClient.servicio.listarMascotas()
    }

    // Obtiene los datos cuando inicia la aplicación
    LaunchedEffect(Unit) {
        try {
            cargarMascotas()
            error = ""
        } catch (e: Exception) {
            Log.e("MASCOTAS_API", "Error al cargar mascotas", e)
            error = "No se pudieron cargar las mascotas: ${e.message}"
        } finally {
            cargando = false
        }
    }

    // Formulario para registrar o editar mascotas
    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = {
                if (!procesando) {
                    mostrarFormulario = false
                    limpiarFormulario()
                }
            },
            title = {
                Text(
                    if (idEditando == null)
                        "Registrar mascota"
                    else
                        "Editar mascota"
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = especie,
                        onValueChange = { especie = it },
                        label = { Text("Especie") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = raza,
                        onValueChange = { raza = it },
                        label = { Text("Raza") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = edad,
                        onValueChange = { edad = it },
                        label = { Text("Edad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        val edadNumero = edad.toIntOrNull()

                        if (
                            nombre.isBlank() ||
                            especie.isBlank() ||
                            raza.isBlank() ||
                            edadNumero == null ||
                            edadNumero < 0
                        ) {
                            error = "Completa todos los campos correctamente"
                            mensaje = ""
                        } else {
                            scope.launch {
                                procesando = true
                                error = ""
                                mensaje = ""

                                try {
                                    val mascota = MascotaModel(
                                        id = idEditando ?: 0,
                                        nombre = nombre.trim(),
                                        especie = especie.trim(),
                                        raza = raza.trim(),
                                        edad = edadNumero
                                    )

                                    val idActual = idEditando

                                    if (idActual == null) {
                                        // POST: Crear mascota
                                        RetrofitClient.servicio
                                            .crearMascota(mascota)

                                        mensaje = "Mascota registrada correctamente"
                                    } else {
                                        // PUT: Actualizar mascota
                                        RetrofitClient.servicio
                                            .actualizarMascota(
                                                idActual,
                                                mascota
                                            )

                                        mensaje = "Mascota actualizada correctamente"
                                    }

                                    cargarMascotas()
                                    mostrarFormulario = false
                                    limpiarFormulario()

                                } catch (e: Exception) {
                                    Log.e(
                                        "MASCOTAS_API",
                                        "Error al guardar mascota",
                                        e
                                    )
                                    error = "No se pudo guardar: ${e.message}"
                                } finally {
                                    procesando = false
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        when {
                            procesando -> "Guardando..."
                            idEditando == null -> "Guardar"
                            else -> "Actualizar"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        mostrarFormulario = false
                        limpiarFormulario()
                    }
                ) {
                    Text("Cancelar")
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = !procesando,
                dismissOnClickOutside = !procesando
            )
        )
    }

    // Confirmación para eliminar una mascota
    mascotaEliminar?.let { mascota ->
        AlertDialog(
            onDismissRequest = {
                if (!procesando) mascotaEliminar = null
            },
            title = { Text("Eliminar mascota") },
            text = {
                Text("¿Estás seguro de eliminar a ${mascota.nombre}?")
            },
            confirmButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = {
                        scope.launch {
                            procesando = true
                            error = ""
                            mensaje = ""

                            try {
                                // DELETE: Eliminar mascota
                                RetrofitClient.servicio
                                    .eliminarMascota(mascota.id)

                                // Actualiza la lista
                                cargarMascotas()

                                mensaje = "Mascota eliminada correctamente"

                            } catch (e: Exception) {
                                Log.e(
                                    "MASCOTAS_API",
                                    "Error al eliminar mascota",
                                    e
                                )
                                error = "No se pudo eliminar: ${e.message}"
                            } finally {
                                mascotaEliminar = null
                                procesando = false
                            }
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !procesando,
                    onClick = { mascotaEliminar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Pantalla principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Mis Mascotas 🐾",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Abre el formulario de registro
        Button(
            onClick = {
                limpiarFormulario()
                error = ""
                mensaje = ""
                mostrarFormulario = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !procesando
        ) {
            Text("Agregar mascota")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mascotas registradas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // GET: Muestra las mascotas
        if (cargando) {
            CircularProgressIndicator()
        } else if (mascotas.isEmpty()) {
            Text("Todavía no hay mascotas registradas")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = mascotas,
                    key = { it.id }
                ) { mascota ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = mascota.nombre,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Especie: ${mascota.especie}")
                            Text("Raza: ${mascota.raza}")
                            Text("Edad: ${mascota.edad} años")

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                // Abre el formulario con los datos existentes
                                OutlinedButton(
                                    enabled = !procesando,
                                    onClick = {
                                        idEditando = mascota.id
                                        nombre = mascota.nombre
                                        especie = mascota.especie
                                        raza = mascota.raza
                                        edad = mascota.edad.toString()

                                        error = ""
                                        mensaje = ""
                                        mostrarFormulario = true
                                    }
                                ) {
                                    Text("Editar")
                                }

                                // Abre la confirmación para eliminar
                                OutlinedButton(
                                    enabled = !procesando,
                                    onClick = {
                                        mascotaEliminar = mascota
                                    }
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
