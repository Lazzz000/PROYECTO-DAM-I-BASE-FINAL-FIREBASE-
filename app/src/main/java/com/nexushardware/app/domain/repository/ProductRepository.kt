package com.nexushardware.app.domain.repository

import com.nexushardware.app.domain.model.Producto

interface ProductRepository {
    suspend fun getProducts(): List<Producto>
}