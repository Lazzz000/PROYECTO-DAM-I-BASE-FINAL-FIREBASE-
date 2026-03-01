package com.nexushardware.app.data.model

data class User (
    val id: String,
    val email: String?,
    val nombreCompleto: String?,
    val telefono: String?,
    val photo: String?,
    val esAdmin: Boolean
)