
package com.tecsup.mascotasapp.data

// Representa los datos de una mascota de nuestra API
data class MascotaModel(
    val id: Int = 0,
    val nombre: String,
    val especie: String,
    val raza: String,
    val edad: Int
)
