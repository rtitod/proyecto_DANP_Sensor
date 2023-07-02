package com.example.proyecto_danp.entities

import com.example.proyecto_danp.entities.SensorRegister

data class SensorRegisterContainer(
    val registers: List<SensorRegister>,
    val start: Int,
    val max: Int,
    val lastRegistroId: Int
    )
