package com.example.proyecto_danp.entities

data class LlaveRegisterContainer(
    val registers: List<LlaveRegister>,
    val start: Int,
    val max: Int,
    val lastRegistroId: Int
)
