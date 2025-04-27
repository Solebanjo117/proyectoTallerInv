import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.R.id
import com.example.proyecto.clientes
import com.example.proyecto.models.Cliente
class ClienteAdapter(private var clientes: List<Cliente>) :
    RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreText: TextView = itemView.findViewById(R.id.textNombreCliente)
        val telefonoText: TextView = itemView.findViewById(R.id.textTelefonoCliente)
        val direccionText: TextView = itemView.findViewById(R.id.textDireccionCliente)
        val deudaText: TextView = itemView.findViewById(R.id.textDeudaCliente)
        val btnPagarDeuda: Button = itemView.findViewById(R.id.btnPagarDeuda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }


    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.nombreText.text = cliente.nombre
        holder.telefonoText.text = cliente.telefono ?: "Telefono no disponible"
        holder.direccionText.text = cliente.direccion ?: "Direccion no disponible"

        if (cliente.deuda > 0) {
            holder.deudaText.visibility = View.VISIBLE
            holder.btnPagarDeuda.visibility = View.VISIBLE
            holder.deudaText.text = "Deuda: $${cliente.deuda}"

            holder.btnPagarDeuda.setOnClickListener {
                val activity = holder.itemView.context as? FragmentActivity
                activity?.supportFragmentManager?.fragments?.forEach { fragment ->
                    if (fragment is clientes) {
                        fragment.mostrarDialogoPago(cliente)
                    }
                }


            }
        } else {
            holder.deudaText.visibility = View.GONE
            holder.btnPagarDeuda.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = clientes.size
    fun actualizarLista(nuevaLista: List<Cliente>) {
        clientes = nuevaLista
        notifyDataSetChanged()
    }

}

