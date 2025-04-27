package com.example.proyecto

import ClienteAdapter
import android.app.AlertDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.models.Cliente

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [clientes.newInstance] factory method to
 * create an instance of this fragment.
 */
class clientes : Fragment() {
    private lateinit var btnAgregarCliente: Button
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var clienteAdapter: ClienteAdapter
    private lateinit var database: SQLiteDatabase
    private val listaClientes = mutableListOf<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate la vista primero.
        return inflater.inflate(R.layout.fragment_clientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAgregarCliente = view.findViewById<Button>(R.id.newClientButton)
        btnAgregarCliente.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, clientes_registro())
                .addToBackStack(null)
                .commit()
        }
        recyclerView = view.findViewById(R.id.clientsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Asumiendo que tienes una clase que extiende de SQLiteOpenHelper llamada BaseDatos
        val dbHelper = DBHelper(requireContext())
        database = dbHelper.readableDatabase
        cargarClientes()

        clienteAdapter = ClienteAdapter(listaClientes)
        recyclerView.adapter = clienteAdapter

        val searchEditText = view.findViewById<EditText>(R.id.searchClientEditText)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(texto: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarClientes(texto.toString())
            }
        })

    }
    private fun cargarClientes() {
        listaClientes.clear()

        val cursor = database.rawQuery("SELECT * FROM clientes", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val telefono = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("telefono"))
                val direccion = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("direccion"))
                val deuda = cursor.getDouble(cursor.getColumnIndexOrThrow("deuda"))

                val cliente = Cliente(id, nombre, telefono, direccion, deuda)
                listaClientes.add(cliente)

            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    private fun android.database.Cursor.getStringOrNull(columnIndex: Int): String? {
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }
    fun mostrarDialogoPago(cliente: Cliente) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_pago_deuda, null)

        val textDeudaActual = view.findViewById<TextView>(R.id.textDeudaActual)
        val editMontoPago = view.findViewById<EditText>(R.id.editMontoPago)

        textDeudaActual.text = "Deuda actual: $${cliente.deuda}"

        builder.setView(view)
            .setTitle("Pagar Deuda")
            .setPositiveButton("Pagar") { dialog, _ ->
                val montoStr = editMontoPago.text.toString()
                val monto = montoStr.toDoubleOrNull()

                if (monto != null && monto > 0) {
                    val nuevaDeuda = (cliente.deuda - monto).coerceAtLeast(0.0)
                    val dbHelper = DBHelper(requireContext())
                    val db = dbHelper.writableDatabase

                    // Actualizar deuda en clientes
                    val contentValues = ContentValues().apply {
                        put("deuda", nuevaDeuda)
                    }
                    db.update(
                        "clientes",
                        contentValues,
                        "id_cliente = ?",
                        arrayOf(cliente.id.toString())
                    )

                    // Insertar en pagos
                    val pagoValues = ContentValues().apply {
                        put("id_cliente", cliente.id)
                        put("monto", monto)
                        // No es necesario poner fecha, la base de datos pone automÃ¡ticamente la actual
                    }
                    db.insert("pagos", null, pagoValues)

                    Toast.makeText(requireContext(), "Pago registrado y deuda actualizada", Toast.LENGTH_SHORT).show()
                    cargarClientes()
                    clienteAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Monto invÃ¡lido", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun filtrarClientes(texto: String) {
        val listaFiltrada = listaClientes.filter {
            it.nombre.contains(texto, ignoreCase = true) ||
                    it.telefono?.contains(texto, ignoreCase = true) == true ||
                    it.direccion?.contains(texto, ignoreCase = true) == true
        }
        clienteAdapter.actualizarLista(listaFiltrada)
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment clientes.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            clientes().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}