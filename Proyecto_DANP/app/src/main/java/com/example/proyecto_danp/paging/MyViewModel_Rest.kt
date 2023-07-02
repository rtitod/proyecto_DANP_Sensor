package com.example.laboratorio5.paging

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.proyecto_danp.entities.SensorRegister
import com.example.proyecto_danp.paging.MyPagingSource_rest
import kotlinx.coroutines.flow.Flow

class MyViewModel_Rest() : ViewModel() {

    fun getData(): Flow<PagingData<SensorRegister>> {
        return Pager(config = PagingConfig(pageSize = 10)) {
            MyPagingSource_rest()
        }.flow
    }
}