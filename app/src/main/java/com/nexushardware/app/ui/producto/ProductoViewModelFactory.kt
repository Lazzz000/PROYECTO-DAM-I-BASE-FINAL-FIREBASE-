package com.nexushardware.app.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nexushardware.app.domain.usecase.GetProductsUseCase

class ProductoViewModelFactory (
    private val useCase: GetProductsUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}