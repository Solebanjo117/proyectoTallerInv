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
    private lateinit var dbHelper: DBHelper
    private lateinit var scannerView: CodeScannerView
    private lateinit var codeScanner: CodeScanner
    private lateinit var codigoEditText: EditText
    private lateinit var escanearButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_productos_registro, container, false)
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
        scannerView = view.findViewById(R.id.codeScannerView)
        codeScanner = CodeScanner(requireContext(), scannerView)
        // Configurar el callback del escaneo
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                codigoBarrasEditText.setText(it.text)
                scannerView.visibility = View.GONE
                codeScanner.stopPreview()
            }
        }

        // Si hay error con la cÃ¡mara
        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Error al usar la cÃ¡mara: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        // BotÃ³n para comenzar a escanear
        escanearButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

                scannerView.visibility = View.VISIBLE
                codeScanner.startPreview()

            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    101
                )
            }
        }

        // Detener escÃ¡ner al pausar el fragmento
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }




        cancelarButton.setOnClickListener {
            parentFragmentManager.popBackStack() // Esto regresa al fragmento anterior (ej. inventario)
        }

        guardarButton.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("nombre", nombreEditText.text.toString())
                put("talla", tallaEditText.text.toString())
                put("color", colorEditText.text.toString())
                put("precio", precioEditText.text.toString().toDoubleOrNull() ?: 0.0)
                put("cantidad", cantidadEditText.text.toString().toIntOrNull() ?: 0)
                put("categoria", categoriaEditText.text.toString())
                put("codigoBarras", codigoBarrasEditText.text.toString())
            }

            val result = db.insert("productos", null, values)
            if (result != -1L) {
                Toast.makeText(requireContext(), "Producto guardado correctamente.", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Regresar al fragment anterior
            } else {
                Toast.makeText(requireContext(), "Error al guardar el producto.", Toast.LENGTH_SHORT).show()
            }
        }



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