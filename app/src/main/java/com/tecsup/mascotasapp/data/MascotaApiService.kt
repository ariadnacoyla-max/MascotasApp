
package com.tecsup.mascotasapp.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Define las operaciones CRUD de la API Django
interface MascotaApiService {

    // GET: Obtener todas las mascotas
    @GET("api/mascotas/")
    suspend fun listarMascotas(): List<MascotaModel>

    // GET: Obtener una mascota por su ID
    @GET("api/mascotas/{id}/")
    suspend fun obtenerMascota(
        @Path("id") id: Int
    ): MascotaModel

    // POST: Registrar una nueva mascota
    @POST("api/mascotas/")
    suspend fun crearMascota(
        @Body mascota: MascotaModel
    ): MascotaModel

    // PUT: Actualizar una mascota
    @PUT("api/mascotas/{id}/")
    suspend fun actualizarMascota(
        @Path("id") id: Int,
        @Body mascota: MascotaModel
    ): MascotaModel

    // DELETE: Eliminar una mascota
    @DELETE("api/mascotas/{id}/")
    suspend fun eliminarMascota(
        @Path("id") id: Int
    )
}
