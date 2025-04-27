package com.example.proyecto.models

data class DetalleVenta(
    val idProducto: Int,
    val nombreProducto: String, // Vamos a traerlo de la tabla productos
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

