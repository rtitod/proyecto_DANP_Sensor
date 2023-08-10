package com.example.proyecto_danp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyecto_danp.operations.getData_Retrofit_lastkey
import com.example.proyecto_danp.operations.postData_Retrofit
import com.example.proyecto_danp.selector.PantallasExistentes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun Formulario(navController: NavHostController) {
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
            }
        }
    ) {
    Column {
        Text(
            text = "Ingresar Parametros para Abrir la Llave",
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
        verticalArrangement = Arrangement.Center)
    {
        var nivelllave by remember { mutableStateOf("") }
        var tiempoapertura by remember { mutableStateOf("") }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Nivel de Apertura"
        )
        OutlinedTextField(
            value = nivelllave,
            onValueChange = { nivelllave = it },
            label = { Text("Ingrese Nivel de Apertura") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Tiempo de Apertura"
        )
        OutlinedTextField(
            value = tiempoapertura,
            onValueChange = { tiempoapertura = it },
            label = { Text("Ingrese el Tiempo de Apertura") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = {
            if(nivelllave.isNotBlank() && tiempoapertura.isNotBlank())
            {
                val currentDate = Date()
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                val formattedDate = dateFormatter.format(currentDate)
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val lastRegistroId: Int? = getData_Retrofit_lastkey()
                        if (lastRegistroId != null) {
                            postData_Retrofit(
                                lastRegistroId + 1,
                                nivelllave.toInt(),
                                tiempoapertura.toInt(),
                                formattedDate
                            )
                        }
                    }
                }
                navController.navigate(route = PantallasExistentes.SwitcherScreen.route)
            }

        }) {
            Text(text = "Ingresar Datos")
        }
    }
}
}
