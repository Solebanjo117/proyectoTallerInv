package com.example.proyecto

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.adapters.VentaAdapter
import com.example.proyecto.models.DetalleVenta
import com.example.proyecto.models.Venta
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [consultaVentas.newInstance] factory method to
 * create an instance of this fragment.
 */
class consultaVentas : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ventaAdapter: VentaAdapter
    private lateinit var listaVentas: MutableList<Venta>
    private lateinit var database: SQLiteDatabase

    private lateinit var searchButton: Button
    private lateinit var dateTextView: TextView
    private lateinit var searchClientEditText: EditText
    private lateinit var totalVentasTextView: TextView
    private var fechaSeleccionada: String? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consulta_ventas, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.salesRecyclerView)
        searchButton = view.findViewById(R.id.searchButton)
        dateTextView = view.findViewById(R.id.dateTextView)
        searchClientEditText = view.findViewById(R.id.searchClientEditText)
        totalVentasTextView = view.findViewById(R.id.totalVentasTextView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listaVentas = mutableListOf()
        ventaAdapter = VentaAdapter(listaVentas) { ventaSeleccionada ->
            mostrarDetallesVenta(ventaSeleccionada)
        }
        recyclerView.adapter = ventaAdapter
        //neta vtalv mucho yarik
        val dbHelper = DBHelper(requireContext())
        database = dbHelper.readableDatabase

        // Cargar todas las ventas al inicio
        cargarVentas()

        dateTextView.setOnClickListener {
            mostrarDatePicker()
        }

        searchButton.setOnClickListener {
            cargarVentas()
        }
    }

    private fun cargarVentas() {
        listaVentas.clear()

        // Base de la consulta
        var query = """
            SELECT ventas.id_venta, ventas.id_cliente, ventas.fecha, ventas.total, clientes.nombre 
            FROM ventas 
            LEFT JOIN clientes ON ventas.id_cliente = clientes.id_cliente
            WHERE 1=1
        """.trimIndent()

        val args = mutableListOf<String>()

        // Filtrar por fecha
        fechaSeleccionada?.let {
            query += " AND DATE(ventas.fecha) = ?"
            args.add(it)
        }

        // Filtrar por nombre de cliente
        val nombreCliente = searchClientEditText.text.toString()
        if (nombreCliente.isNotEmpty()) {
            query += " AND clientes.nombre LIKE ?"
            args.add("%$nombreCliente%")
        }

        val cursor = database.rawQuery(query, args.toTypedArray())

        var totalVentas = 0.0

        if (cursor.moveToFirst()) {
            do {
                val idVenta = cursor.getInt(cursor.getColumnIndexOrThrow("id_venta"))
                val idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                val nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))

                val venta = Venta(
                    idVenta,
                    if (cursor.isNull(cursor.getColumnIndexOrThrow("id_cliente"))) null else cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente")),
                    fecha,
                    total,
                    cursor.getStringOrNull(cursor.getColumnIndexOrThrow("nombre"))
                )

                listaVentas.add(venta)

                totalVentas += total
            } while (cursor.moveToNext())
        }

        cursor.close()

        ventaAdapter.notifyDataSetChanged()

        // Mostrar el total
        totalVentasTextView.text = "Total vendido: $${String.format("%.2f", totalVentas)}"
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val mes = (month + 1).toString().padStart(2, '0')
                val dia = dayOfMonth.toString().padStart(2, '0')
                fechaSeleccionada = "$year-$mes-$dia"
                dateTextView.text = fechaSeleccionada
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun mostrarDetallesVenta(venta: Venta) {
        val dbHelper = DBHelper(requireContext())
        val db = dbHelper.readableDatabase

        val detalles = mutableListOf<DetalleVenta>()

        val query = """
        SELECT p.nombre, d.cantidad, d.precio_unitario, d.subtotal
        FROM detalle_venta d
        INNER JOIN productos p ON d.id_producto = p.id
        WHERE d.id_venta = ?
    """
        val cursor = db.rawQuery(query, arrayOf(venta.idVenta.toString()))

        if (cursor.moveToFirst()) {
            do {
                val nombreProducto = cursor.getString(0)
                val cantidad = cursor.getInt(1)
                val precioUnitario = cursor.getDouble(2)
                val subtotal = cursor.getDouble(3)

                detalles.add(DetalleVenta(0, nombreProducto, cantidad, precioUnitario, subtotal))
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Construir el mensaje para el Dialog
        val builder = StringBuilder()
        builder.append("Cliente: ${venta.nombreCliente ?: "No registrado"}\n")
        builder.append("Fecha: ${venta.fecha}\n")
        builder.append("Total: $${String.format("%.2f", venta.total)}\n\n")
        builder.append("Productos:\n")

        detalles.forEach { detalle ->
            builder.append("- ${detalle.nombreProducto}: ${detalle.cantidad} x $${String.format("%.2f", detalle.precioUnitario)} = $${String.format("%.2f", detalle.subtotal)}\n")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Detalles de Venta")
            .setMessage(builder.toString())
            .setPositiveButton("Cerrar", null)
            .show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment consultaVentas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            consultaVentas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}