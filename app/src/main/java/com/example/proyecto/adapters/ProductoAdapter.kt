package com.example.proyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.models.Producto

class ProductoAdapter(
    private val listaProductos: List<Producto>,
    private val listener: OnProductoClickListener
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    interface OnProductoClickListener {
        fun onEditar(producto: Producto)
        fun onEliminar(producto: Producto)
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre = itemView.findViewById<TextView>(R.id.productoNombre)
        val detalles = itemView.findViewById<TextView>(R.id.productoDetalles)
        val btnEditar = itemView.findViewById<Button>(R.id.btnEditar)
        val btnEliminar = itemView.findViewById<Button>(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.nombre.text = producto.nombre
        holder.detalles.text =
            "${producto.talla} - ${producto.color} - \$${producto.precio} - ${producto.cantidad} piezas"

        holder.btnEditar.setOnClickListener { listener.onEditar(producto) }
        holder.btnEliminar.setOnClickListener { listener.onEliminar(producto) }
    }

    override fun getItemCount(): Int = listaProductos.size
}