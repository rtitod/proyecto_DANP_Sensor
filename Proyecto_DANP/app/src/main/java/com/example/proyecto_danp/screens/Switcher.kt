package com.example.proyecto_danp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyecto_danp.selector.PantallasExistentes


@Composable
fun Switcher(navController: NavHostController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 40.dp, start = 16.dp, bottom = 80.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Menu Principal",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(20.dp)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, start = 16.dp, bottom = 80.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Button(onClick = { navController.navigate(route = PantallasExistentes.FormularioScreen.route)}) {
            Text(text = "Ingresar Datos a la Nube")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { navController.navigate(route = PantallasExistentes.ConsultaScreen.route) }) {
            Text(text = "Consultar Datos")
        }
    }

}