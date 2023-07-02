package com.example.proyecto_danp.api

import com.example.proyecto_danp.entities.SensorRegister
import com.example.proyecto_danp.entities.SensorRegisterContainer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RestApi {
    @Headers("Content-Type: application/json")
    @POST("sensorapi")
    fun crearRegistro(
        @Body sensorinfo: SensorRegister?
    ): Call<SensorRegister?>?
    @GET("sensorapi")
    fun obtenerKeyMax(
        @Query("startregister") start: Int,
        @Query("maxregisters") max: Int
    ): Call<SensorRegisterContainer>
    @GET("sensorapi")
    fun obtenerRegistros(
        @Query("startregister") start: Int,
        @Query("maxregisters") max: Int
    ): Call<SensorRegisterContainer>
}