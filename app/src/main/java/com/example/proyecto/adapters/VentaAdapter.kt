package com.example.proyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.models.Venta

class VentaAdapter(
    private var ventas: List<Venta>,
    private val onClick: (Venta) -> Unit
) : RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

    inner class VentaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCliente: TextView = itemView.findViewById(R.id.textClienteVenta)
        val textFecha: TextView = itemView.findViewById(R.id.textFechaVenta)
        val textTotal: TextView = itemView.findViewById(R.id.textTotalVenta)

        init {
            itemView.setOnClickListener {
                onClick(ventas[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_venta, parent, false)
        return VentaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {
        val venta = ventas[position]
        holder.textCliente.text = venta.nombreCliente
        holder.textFecha.text = venta.fecha
        holder.textTotal.text = "Total: $${venta.total}"
    }

    override fun getItemCount(): Int = ventas.size

    fun actualizarLista(nuevaLista: List<Venta>) {
        ventas = nuevaLista
        notifyDataSetChanged()
    }
}
