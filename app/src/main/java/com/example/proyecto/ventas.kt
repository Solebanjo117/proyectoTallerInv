package com.example.proyecto

import BarcodeScanner
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.codescanner.CodeScannerView
import com.example.proyecto.adapters.CarritoAdapter
import com.example.proyecto.models.ProductoCarrito
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button

class ventas : Fragment() {

    private lateinit var clientAutoComplete: AutoCompleteTextView
    private var selectedClientId: Int? = null
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var carritoAdapter: CarritoAdapter
    private val carrito = mutableListOf<ProductoCarrito>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ventas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productRecyclerView = requireView().findViewById(R.id.productRecyclerView)
        carritoAdapter = CarritoAdapter(carrito)
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productRecyclerView.adapter = carritoAdapter

        val scannerView = view.findViewById<CodeScannerView>(R.id.codeScannerView)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val button = view.findViewById<ImageButton>(R.id.cameraButton)
        val scanner = BarcodeScanner(scannerView, searchEditText, button)
        val receivedMoneyEditText = view.findViewById<EditText>(R.id.receivedAmountEditText)
        scanner.initScanner()
        configurarAutoCompleteClientes()
searchEditText.addTextChangedListener(object : TextWatcher{
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
         val codigo = s.toString().trim()  // Mejor usar 's' directamente aquÃ­
        if (codigo.isNotEmpty()) {
            if(agregarProductoPorCodigo(codigo))

            // Limpiar el campo despuÃ©s de agregar el producto
            searchEditText.text.clear()
        }
    }

})
       /* searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val codigo = searchEditText.text.toString().trim()
                if (codigo.isNotEmpty()) {
                    agregarProductoPorCodigo(codigo)
                }
                true
            } else {
                false
            }
        }*/

        // FunciÃ³n modificada para solo mostrar el cambio
        receivedMoneyEditText.setOnEditorActionListener { v, actionId, event ->
            val receivedAmount = receivedMoneyEditText.text.toString().toDoubleOrNull()

            if (receivedAmount != null) {
                // Calcula el total desde el carrito
                val total = calcularTotalCarrito()

                // Verifica si el monto recibido es suficiente para cubrir el total
                if (receivedAmount >= total) {
                    val change = receivedAmount - total
                    // Actualiza el cambio
                    val changeTextView = requireView().findViewById<TextView>(R.id.changeTextView)
                    changeTextView.text = "Cambio: $${"%.2f".format(change)}"
                    return@setOnEditorActionListener true
                } else {
                    // Monto insuficiente
                    Toast.makeText(requireContext(), "Monto recibido insuficiente", Toast.LENGTH_SHORT).show()
                    return@setOnEditorActionListener false
                }
            } else {
                // Si no se ingresa un monto vÃ¡lido
                Toast.makeText(requireContext(), "Por favor ingrese un monto vÃ¡lido", Toast.LENGTH_SHORT).show()
                return@setOnEditorActionListener false
            }
        }

// ConfiguraciÃ³n del botÃ³n de pago
        val payButton = view.findViewById<Button>(R.id.payButton)
        payButton.setOnClickListener {
            val receivedAmount = receivedMoneyEditText.text.toString().toDoubleOrNull()

            if (receivedAmount != null) {
                // Calcula el total desde el carrito
                val total = calcularTotalCarrito()

                // Verifica si el monto recibido es suficiente para cubrir el total
                if (receivedAmount >= total) {
                    val change = receivedAmount - total
                    // Muestra el cambio
                    val changeTextView = requireView().findViewById<TextView>(R.id.changeTextView)
                    changeTextView.text = "Cambio: $${"%.2f".format(change)}"

                    // Realiza el proceso de venta
                    realizarVenta()
                    Toast.makeText(requireContext(), "Venta realizada con Ã©xito", Toast.LENGTH_SHORT).show()
                } else {
                    // Monto insuficiente
                    Toast.makeText(requireContext(), "Monto recibido insuficiente", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Si no se ingresa un monto vÃ¡lido
                Toast.makeText(requireContext(), "Por favor ingrese un monto vÃ¡lido", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun configurarAutoCompleteClientes() {
        clientAutoComplete = requireView().findViewById(R.id.clientAutoComplete)
        val dbHelper = DBHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT id_cliente, nombre FROM clientes", null)
        val clientList = mutableListOf<Pair<Int, String>>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            clientList.add(Pair(id, nombre))
        }
        cursor.close()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            clientList.map { it.second }
        )

        clientAutoComplete.setAdapter(adapter)

        clientAutoComplete.setOnItemClickListener { parent, _, _, _ ->
            val selectedName = parent.getItemAtPosition(0) as String
            selectedClientId = clientList.find { it.second == selectedName }?.first
        }

    }

    private fun  agregarProductoPorCodigo(codigo: String): Boolean {
        val dbHelper = DBHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT id, nombre, precio FROM productos WHERE codigoBarras = ?", arrayOf(codigo))

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))

            // Verificar si el producto ya existe en el carrito
            val existente = carrito.find { it.id == id }
            if (existente != null) {
                // Si el producto ya existe, aumentar solo la cantidad
                existente.cantidad += 1
            } else {
                // Si no existe, agregar el producto al carrito
                carrito.add(ProductoCarrito(id, nombre, 1, precio))
            }

            // Notificar al adaptador que los datos han cambiado
            carritoAdapter.notifyDataSetChanged()
            // Actualizar el resumen de la venta
            actualizarResumenVenta()
             return true
        } else {
            // Si no se encuentra el producto, mostrar un mensaje de error
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()

        }

        cursor.close()
        return false
    }


    private fun calcularTotalCarrito(): Double {
        var total = 0.0
        for (producto in carrito) {
            total += producto.precio * producto.cantidad
        }
        return total
    }

    private fun actualizarResumenVenta() {
        val total = calcularTotalCarrito()
        val cantidadProductos = carrito.sumBy { it.cantidad }
        val summaryTextView = requireView().findViewById<TextView>(R.id.summaryTextView)
        summaryTextView.text = "Total: $${"%.2f".format(total)} | Productos: $cantidadProductos"
    }

    private fun realizarVenta() {
        // Verificar si el carrito estÃ¡ vacÃ­o
        if (carrito.isEmpty()) {
            Toast.makeText(requireContext(), "El carrito estÃ¡ vacÃ­o. No se puede realizar la venta.", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = DBHelper(requireContext())
        val db = dbHelper.writableDatabase

        // Inserta la venta en la base de datos
        val contentValues = ContentValues().apply {
            put("id_cliente", selectedClientId)
            put("fecha", System.currentTimeMillis())
            put("total", calcularTotalCarrito())
        }

        val ventaId = db.insert("ventas", null, contentValues)

        // Inserta los productos vendidos en la tabla detalle_venta
        for (producto in carrito) {
            val productoContentValues = ContentValues().apply {
                put("id_venta", ventaId)
                put("id_producto", producto.id)
                put("cantidad", producto.cantidad)
                put("precio_unitario", producto.precio)
                put("subtotal", producto.precio * producto.cantidad)
            }
            db.insert("detalle_venta", null, productoContentValues)

            // Actualiza la cantidad de producto en la tabla productos
            val nuevaCantidad = producto.cantidad // La cantidad vendida
            val updateValues = ContentValues().apply {
                put("cantidad", "cantidad - $nuevaCantidad")  // Disminuye la cantidad
            }

            // Ejecuta la actualizaciÃ³n de la cantidad de producto
            db.execSQL("UPDATE productos SET cantidad = cantidad - ? WHERE id = ?", arrayOf(nuevaCantidad, producto.id))
        }

        // Vaciar el carrito despuÃ©s de realizar la venta
        if (selectedClientId != null) {
            db.execSQL(
                "UPDATE clientes SET deuda = deuda + ? WHERE id_cliente = ?",
                arrayOf(calcularTotalCarrito(), selectedClientId)
            )
        }
        carrito.clear()
        carritoAdapter.notifyDataSetChanged()
        actualizarResumenVenta()

        Toast.makeText(requireContext(), "Venta realizada con Ã©xito", Toast.LENGTH_SHORT).show()
        limpiarCampos()
        // Actualizar la deuda del cliente


    }


    private fun limpiarCampos() {
        // Limpiar el campo de monto recibido
        val receivedMoneyEditText = requireView().findViewById<EditText>(R.id.receivedAmountEditText)
        receivedMoneyEditText.text.clear()
        val searchEditText = requireView().findViewById<EditText>(R.id.searchEditText)
        searchEditText.text.clear()
        // Limpiar el campo de cambio
        val changeTextView = requireView().findViewById<TextView>(R.id.changeTextView)
        changeTextView.text = "Cambio: $0.00"
        selectedClientId = null
        clientAutoComplete.setText("")

        // Limpiar el carrito (opcional, ya se hace en realizarVenta, pero si quieres ser explÃ­cito)
        carrito.clear()

        // Notificar al adaptador de que el carrito estÃ¡ vacÃ­o
        carritoAdapter.notifyDataSetChanged()

        // Limpiar cualquier otro campo si es necesario
        actualizarResumenVenta() // Esto puede restablecer cualquier resumen relacionado con la venta
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ventas().apply {
            }
    }
}
