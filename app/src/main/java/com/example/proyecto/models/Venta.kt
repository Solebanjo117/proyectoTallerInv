package com.example.proyecto.models

data class Venta(
    val idVenta: Int,
    val idCliente: Int?,
    val fecha: String,
    val total: Double,
    val nombreCliente: String?
)

