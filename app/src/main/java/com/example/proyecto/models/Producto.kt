package com.example.proyecto.models

data class Producto(
    val id: Int,
    val nombre: String,
    val talla: String,
    val color: String,
    val precio: Double,
    val cantidad: Int,
    val categoria: String,
    val codigoBarras: String
)
