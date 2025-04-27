package com.example.proyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.models.ProductoCarrito

class CarritoAdapter(private val carrito: List<ProductoCarrito>) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreProducto: TextView = view.findViewById(R.id.nombreProducto)
        val cantidadProducto: TextView = view.findViewById(R.id.cantidadProducto)
        val precioProducto: TextView = view.findViewById(R.id.precioProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = carrito[position]
        holder.nombreProducto.text = producto.nombre
        holder.cantidadProducto.text = "Cantidad: ${producto.cantidad}"
        holder.precioProducto.text = "Precio: \$${String.format("%.2f", producto.precio * producto.cantidad)}"
    }

    override fun getItemCount(): Int = carrito.size
}
