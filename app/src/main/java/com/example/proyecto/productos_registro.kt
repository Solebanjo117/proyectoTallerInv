package com.example.proyecto
import BarcodeScanner
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.example.proyecto.databinding.FragmentProductosRegistroBinding
import com.example.proyecto.models.Producto

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [productos_registro.newInstance] factory method to
 * create an instance of this fragment.
 */
class productos_registro : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentProductosRegistroBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DBHelper
    private lateinit var scannerView: CodeScannerView
    private lateinit var codeScanner: CodeScanner
    private lateinit var codigoEditText: EditText
    private lateinit var escanearButton: ImageButton
    private lateinit var barScanner : BarcodeScanner
    private var producto: Producto? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductosRegistroBinding.inflate(inflater, container, false)

        barScanner = BarcodeScanner(
            binding.codeScannerView,
            binding.codigoBarrasEditText,
            binding.btnEscanear
        )
        barScanner.initScanner()

        return binding.root

    }
    override fun onPause() {
        super.onPause()
        barScanner.stopScanner()  // Detener el escÃ¡ner cuando se pausa el fragmento
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())

        val nombreEditText = view.findViewById<EditText>(R.id.nombreProductoEditText)
        val tallaEditText = view.findViewById<EditText>(R.id.tallaProductoEditText)
        val colorEditText = view.findViewById<EditText>(R.id.colorProductoEditText)
        val precioEditText = view.findViewById<EditText>(R.id.precioProductoEditText)
        val cantidadEditText = view.findViewById<EditText>(R.id.cantidadProductoEditText)
        val categoriaEditText = view.findViewById<EditText>(R.id.categoriaProductoEditText)
        val codigoBarrasEditText = view.findViewById<EditText>(R.id.codigoBarrasEditText)

        val guardarButton = view.findViewById<Button>(R.id.guardarProductoButton)
        val cancelarButton = view.findViewById<Button>(R.id.cancelarButton)
        val escanearButton = view.findViewById<ImageButton>(R.id.btnEscanear)
        val producto = arguments?.getParcelable<Producto>("producto")
        val esEdicion = arguments?.getBoolean("esEdicion", false) ?: false

        producto?.let {
            binding.nombreProductoEditText.setText(it.nombre)
            binding.tallaProductoEditText.setText(it.talla)
            binding.colorProductoEditText.setText(it.color)
            binding.precioProductoEditText.setText(it.precio.toString())
            binding.cantidadProductoEditText.setText(it.cantidad.toString())
            binding.categoriaProductoEditText.setText(it.categoria)
            binding.codigoBarrasEditText.setText(it.codigoBarras)

            if (esEdicion) {
                binding.guardarProductoButton.text = "Actualizar producto"
            }
        }

        cancelarButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, inventario())  // Nueva instancia de inventario
                .commit()
        }

        guardarButton.setOnClickListener {
            val nombre = binding.nombreProductoEditText.text.toString()
            val talla = binding.tallaProductoEditText.text.toString()
            val color = binding.colorProductoEditText.text.toString()
            val precio = binding.precioProductoEditText.text.toString().toDoubleOrNull()
            val cantidad = binding.cantidadProductoEditText.text.toString().toIntOrNull()
            val categoria = binding.categoriaProductoEditText.text.toString()
            val codigoBarras = binding.codigoBarrasEditText.text.toString()

            if (nombre.isNotEmpty() && talla.isNotEmpty() && color.isNotEmpty() &&
                precio != null && cantidad != null && categoria.isNotEmpty()) {

                val productoFinal = Producto(
                    id = producto?.id ?: 0, // Si es 0, asumimos que es nuevo
                    nombre ,talla ,color,precio,cantidad,categoria,codigoBarras)

                if (producto == null) {
                    dbHelper.insertProducto(productoFinal)
                    Toast.makeText(requireContext(), "Producto registrado", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.updateProducto(productoFinal)
                    Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, inventario())  // Nueva instancia de inventario
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment productos_registro.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            productos_registro().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}