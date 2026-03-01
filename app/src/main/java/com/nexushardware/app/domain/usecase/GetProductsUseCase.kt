package com.nexushardware.app.domain.usecase

import com.nexushardware.app.domain.model.Producto
import com.nexushardware.app.domain.repository.ProductRepository

class GetProductsUseCase (
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): List<Producto> {
        return repository.getProducts()
    }
}