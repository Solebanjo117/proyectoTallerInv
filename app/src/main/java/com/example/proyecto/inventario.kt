package com.example.proyecto

import BarcodeScanner
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.codescanner.CodeScannerView
import com.example.proyecto.adapters.ProductoAdapter
import com.example.proyecto.models.Producto

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [inventario.newInstance] factory method to
 * create an instance of this fragment.
 */
class inventario : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var newProductButton: Button
    private lateinit var dbHelper: SQLiteOpenHelper
    private lateinit var barScanner : BarcodeScanner
    private var listaProductos = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.inventoryRecyclerView)
        searchEditText = view.findViewById(R.id.searchProductEditText)
        newProductButton = view.findViewById(R.id.newProductButton)
        val scanButton = view.findViewById<ImageButton>(R.id.scanBarcodeButton)
        val codeScannerView = view.findViewById<CodeScannerView>(R.id.codeScannerView)
        barScanner = BarcodeScanner(codeScannerView,searchEditText,scanButton)
        barScanner.initScanner()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dbHelper = DBHelper(requireContext())

        cargarProductos()

        newProductButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, productos_registro())
                .commit()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString()
                filtrarProductos(texto)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }
    override fun onPause() {
        super.onPause()
        barScanner.stopScanner()  // Detener el escÃ¡ner cuando se pausa el fragmento
    }

    private fun cargarProductos() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM productos", null)

        listaProductos.clear()

        if (cursor.moveToFirst()) {
            do {
                Log.d("Inventario", "Productos: ${cursor.getColumnIndexOrThrow("nombre")}")

                listaProductos.add(
                    Producto(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("talla")),
                        cursor.getString(cursor.getColumnIndexOrThrow("color")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                        cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                        cursor.getString(cursor.getColumnIndexOrThrow("codigoBarras"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        recyclerView.adapter = ProductoAdapter(listaProductos, object : ProductoAdapter.OnProductoClickListener {
            override fun onEditar(producto: Producto) {
                val bundle = Bundle().apply {
                    putParcelable("producto", producto)
                    putBoolean("esEdicion", true)
                }

                val fragment = productos_registro().apply {
                    arguments = bundle
                }

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()

            }

            override fun onEliminar(producto: Producto) {
                val db = dbHelper.writableDatabase
                db.delete("productos", "id = ?", arrayOf(producto.id.toString()))
                cargarProductos()  // Recarga la lista
                Toast.makeText(requireContext(), "${producto.nombre} eliminado", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun filtrarProductos(texto: String) {
        val productosFiltrados = listaProductos.filter {
            it.nombre.contains(texto, ignoreCase = true) ||
                    it.codigoBarras.contains(texto, ignoreCase = true)
        }
        recyclerView.adapter = ProductoAdapter(productosFiltrados, object : ProductoAdapter.OnProductoClickListener {
            override fun onEditar(producto: Producto) {
                Toast.makeText(requireContext(), "Editar: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }

            override fun onEliminar(producto: Producto) {
                val db = dbHelper.writableDatabase
                db.delete("productos", "id = ?", arrayOf(producto.id.toString()))
                cargarProductos()
                Toast.makeText(requireContext(), "${producto.nombre} eliminado", Toast.LENGTH_SHORT).show()
            }
        })

    }
}