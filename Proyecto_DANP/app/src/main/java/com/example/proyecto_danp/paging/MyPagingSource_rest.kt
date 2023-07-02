package com.example.proyecto_danp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.proyecto_danp.entities.SensorRegister
import com.example.proyecto_danp.entities.SensorRegisterContainer
import com.example.proyecto_danp.operations.getData_Retrofit_all
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyPagingSource_rest() : PagingSource<Int, SensorRegister>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SensorRegister> {
            return try {
                val startregister = params.key ?: 1
                val pageSize = 10
                val sensorDataItems = convertToSensorDataItems(startregister,pageSize)
                val nextPageKey = if (sensorDataItems.isNotEmpty()) startregister + pageSize else null
                LoadResult.Page(
                    data = sensorDataItems,
                    prevKey = null,
                    nextKey = nextPageKey
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, SensorRegister>): Int? {
            return state.anchorPosition
        }
        private suspend fun convertToSensorDataItems(startregister:Int,maxregisters:Int): List<SensorRegister> {
            val SensorReturn = mutableListOf<SensorRegister>()
            var sensorRegisterContainer: SensorRegisterContainer? = null
            withContext(Dispatchers.IO) {
                sensorRegisterContainer = getData_Retrofit_all(startregister,maxregisters)
            }
            sensorRegisterContainer?.registers?.forEach { sensorRegister ->
                SensorReturn.add(
                    SensorRegister(
                    sensorRegister.RegistroId,
                    sensorRegister.FechayHora,
                    sensorRegister.medida,
                    sensorRegister.comentario)
                )
            }
        return SensorReturn.toList()
        }
    }
