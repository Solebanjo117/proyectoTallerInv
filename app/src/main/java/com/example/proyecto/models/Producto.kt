package com.example.proyecto.models
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
@Parcelize
data class Producto(
    val id: Int,
    val nombre: String,
    val talla: String,
    val color: String,
    val precio: Double,
    val cantidad: Int,
    val categoria: String,
    val codigoBarras: String
): Parcelable
