package com.example.proyecto_danp.mycomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyecto_danp.entities.SensorRegister

@Composable
fun SensorDataItemCard(
    sensorRegister: SensorRegister
){

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { }
            .fillMaxWidth(),
        elevation = 10.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.background(Color.LightGray)
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(text = "Fecha: " + sensorRegister.FechayHora)
            Text(text = "Medicion temperatura: " + sensorRegister.medida)
            Text(text = "Comentario: " + sensorRegister.comentario)
        }

    }

}