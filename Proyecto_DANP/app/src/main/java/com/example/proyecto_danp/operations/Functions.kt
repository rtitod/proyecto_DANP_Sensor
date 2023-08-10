package com.example.proyecto_danp.operations

import android.util.Log
import com.example.proyecto_danp.api.RestApi
import com.example.proyecto_danp.entities.LlaveRegister
import com.example.proyecto_danp.entities.SensorRegister
import com.example.proyecto_danp.entities.SensorRegisterContainer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

fun getData_Retrofit_all(startregister: Int, maxregisters:Int): SensorRegisterContainer? {

    val url = "https://qap9opok49.execute-api.us-west-2.amazonaws.com/prod/"
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofitAPI = retrofit.create(RestApi::class.java)

    try {
        val response = retrofitAPI.obtenerRegistros(startregister, maxregisters).execute()
        if (response.isSuccessful) {
            val registersResponse = response.body()
            val lastRegistroId = registersResponse
            if (lastRegistroId != null) {
                Log.d("GET_ALL_REGISTROS", lastRegistroId.registers.toString())
            }
            return lastRegistroId
        } else {
            val errorCode = response.code()
            Log.d("GET_ALL_ERROR_CODE", "Código de error: $errorCode")
        }
    } catch (e: IOException) {
        Log.d("GET_ALL_IO_EX", "error")
    }

    return null
}

fun postData_Retrofit(
    RegistroId: Int,
    nivelgrifo: Int,
    tiempoapertura: Int,
    fechayhora: String
) {
    var url = "https://t473ll27a2.execute-api.us-west-2.amazonaws.com/prod/"
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofitAPI = retrofit.create(RestApi::class.java)
    val dataModel = LlaveRegister(RegistroId,nivelgrifo,tiempoapertura,fechayhora )
    val call: Call<LlaveRegister?>? = retrofitAPI.crearRegistro(dataModel)
    call!!.enqueue(object : Callback<LlaveRegister?> {
        override fun onResponse(call: Call<LlaveRegister?>?, response: Response<LlaveRegister?>) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("POST_SUCCESS", "Respuesta exitosa")
            } else {
                val errorCode = response.code()
                Log.d("POST_ERROR", "Código de error: $errorCode")
            }
        }

        override fun onFailure(call: Call<LlaveRegister?>?, t: Throwable) {
            Log.e("POST_FAILURE", "Error en la solicitud: ${t.message}")
        }
    })
}

fun getData_Retrofit_lastkey(): Int? {
    val url = "https://t473ll27a2.execute-api.us-west-2.amazonaws.com/prod/"
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val retrofitAPI = retrofit.create(RestApi::class.java)

    val startregister = 1
    val maxregisters = 1

    try {
        val response = retrofitAPI.obtenerKeyMax(startregister, maxregisters).execute()
        if (response.isSuccessful) {
            val registersResponse = response.body()
            val lastRegistroId = registersResponse?.lastRegistroId
            Log.d("GET_REGISTROID", lastRegistroId.toString())
            return lastRegistroId
        } else {
            val errorCode = response.code()
            Log.d("GET_ERROR", "Código de error: $errorCode")
        }
    } catch (e: IOException) {
    }

    return null
}