package com.example.proyecto_danp.entities

data class SensorRegister(
    var RegistroId: Int,
    var FechayHora: String,
    var medida: Int,
    var comentario: String
)
