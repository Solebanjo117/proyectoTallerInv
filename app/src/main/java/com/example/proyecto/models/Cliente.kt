package com.example.proyecto.models

data class Cliente(
    val id: Int,
    val nombre: String,
    val telefono: String?,
    val direccion: String?,
    val deuda: Double
)

