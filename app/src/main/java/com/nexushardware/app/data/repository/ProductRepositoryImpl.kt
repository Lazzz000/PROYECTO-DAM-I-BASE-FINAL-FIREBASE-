package com.nexushardware.app.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.nexushardware.app.data.remote.ProductRemoteDataSource
import com.nexushardware.app.domain.model.Producto
import com.nexushardware.app.domain.repository.ProductRepository

class ProductRepositoryImpl (
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {


    override suspend fun getProducts(): List<Producto> {
        return remoteDataSource.getProducts()
    }
}