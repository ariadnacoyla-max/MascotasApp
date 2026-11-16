
package com.tecsup.mascotasapp.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Servicio que conecta Android con la API REST de Django
interface MascotaApiService {

    // Obtiene la lista de mascotas registradas
    @GET("api/mascotas/")
    suspend fun listarMascotas(): List<MascotaModel>

    // Busca una mascota por su ID
    @GET("api/mascotas/{id}/")
    suspend fun obtenerMascota(
        @Path("id") id: Int
    ): Response<MascotaModel>

    // Registra una nueva mascota
    @POST("api/mascotas/")
    suspend fun agregarMascota(
        @Body mascota: MascotaModel
    ): Response<MascotaModel>

    // Actualiza los datos de una mascota
    @PUT("api/mascotas/{id}/")
    suspend fun actualizarMascota(
        @Path("id") id: Int,
        @Body mascota: MascotaModel
    ): Response<MascotaModel>

    // Elimina una mascota
    @DELETE("api/mascotas/{id}/")
    suspend fun eliminarMascota(
        @Path("id") id: Int
    ): Response<Unit>
}

