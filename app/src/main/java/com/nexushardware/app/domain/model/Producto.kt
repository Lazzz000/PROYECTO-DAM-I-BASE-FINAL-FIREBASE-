package com.nexushardware.app.domain.model

data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val urlImagen: String
)