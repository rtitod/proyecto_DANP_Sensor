package com.example.proyecto_danp.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.laboratorio5.paging.MyViewModel_Rest
import com.example.proyecto_danp.entities.SensorRegister
import com.example.proyecto_danp.mycomponents.SensorDataItemCard

@Composable
fun Consulta(navController: NavHostController) {

    val context = LocalContext.current


    val viewModel: MyViewModel_Rest = remember {
        MyViewModel_Rest()
    }

    val dataItems = remember(viewModel) {
        viewModel.getData().cachedIn(viewModel.viewModelScope)
    }.collectAsLazyPagingItems()

    Column {
        Text(
            text = "Registros de Humedad",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(20.dp)
        )

        LazyColumn {
            items(dataItems) { dataItem ->
                dataItem?.let { item ->
                    Log.e("ITEM_SHOW", "${item.RegistroId}")
                    DataItemRow(dataItem = item)
                }
            }
            dataItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    loadState.append is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val errorMessage = (loadState.refresh as LoadState.Error).error.message
                        item { ErrorItem(errorMessage = errorMessage) }
                    }
                    loadState.append is LoadState.Error -> {
                        val errorMessage = (loadState.append as LoadState.Error).error.message
                        item { ErrorItem(errorMessage = errorMessage) }
                    }
                }
            }
        }
    }
}


@Composable
fun DataItemRow(dataItem: SensorRegister) {
    // Aquí puedes definir el diseño de una fila de item de datos
    SensorDataItemCard(sensorRegister = dataItem)
}

@Composable
fun LoadingItem() {
    // Aquí puedes definir el diseño de un elemento de carga
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

}

@Composable
fun ErrorItem(errorMessage: String?) {
    // Aquí puedes definir el diseño de un elemento de error
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage ?: "Error desconocido",
            style = MaterialTheme.typography.body1,
            color = Color.Red
        )
    }

}

