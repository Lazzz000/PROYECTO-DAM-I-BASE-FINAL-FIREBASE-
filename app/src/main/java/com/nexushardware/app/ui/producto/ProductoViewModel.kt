package com.nexushardware.app.ui.producto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexushardware.app.domain.model.Producto
import com.nexushardware.app.domain.usecase.GetProductsUseCase
import com.nexushardware.app.utils.AgregarListaProductos
import kotlinx.coroutines.launch

class ProductoViewModel (
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _productos = MutableLiveData<List<Producto>>()
    val productos: LiveData<List<Producto>> = _productos

    private var listaOriginal: List<Producto> = emptyList()


    fun cargarProductos() {
        viewModelScope.launch {
            val lista = getProductsUseCase()
            listaOriginal = lista
            _productos.value = lista
            if(!lista.isNotEmpty()){
                AgregarListaProductos.insertarProductosDemo()
            }

        }
    }
    fun filtrarProductos(query: String) {
        if (query.isEmpty()) {
            _productos.value = listaOriginal
        } else {
            _productos.value = listaOriginal.filter {
                it.nombre.contains(query, ignoreCase = true)
            }
        }
    }
    fun filtrarCategoriaProductos(query: String) {
        if (query.isEmpty() || query == "todos") {
            _productos.value = listaOriginal
        } else {
            _productos.value = listaOriginal.filter {
                it.categoria.contains(query, ignoreCase = true)
            }
        }
    }

    fun obtenerProductoPorId(idBuscado: String): Producto? {
        // devolvemos el primer objeto que sea igual al id
        return listaOriginal.find { it.id == idBuscado }
    }
}