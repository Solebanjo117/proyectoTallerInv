package com.example.proyecto.models

data class ProductoCarrito(
    val id: Int,
    val nombre: String,
    var cantidad: Int,
    val precio: Double
)

