package com.example.proyecto_danp.api

import com.example.proyecto_danp.entities.LlaveRegister
import com.example.proyecto_danp.entities.LlaveRegisterContainer
import com.example.proyecto_danp.entities.SensorRegisterContainer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RestApi {
    @Headers("Content-Type: application/json")
    @POST("llaveapi")
    fun crearRegistro(
        @Body llaveinfo: LlaveRegister?
    ): Call<LlaveRegister?>?
    @GET("llaveapi")
    fun obtenerKeyMax(
        @Query("startregister") start: Int,
        @Query("maxregisters") max: Int
    ): Call<LlaveRegisterContainer>
    @GET("sensorapi")
    fun obtenerRegistros(
        @Query("startregister") start: Int,
        @Query("maxregisters") max: Int
    ): Call<SensorRegisterContainer>
}