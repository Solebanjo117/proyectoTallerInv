import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import android.Manifest
import com.example.proyecto.R
class BarcodeScanner(private val scannerView: CodeScannerView,
                     private val codigoBarrasEditText: EditText,
                     private val escanearButton: ImageButton
) : Fragment(

) {

    private lateinit var codeScanner: CodeScanner
    fun initScanner() {
        codeScanner = CodeScanner(scannerView.context, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            scannerView.post {
                codigoBarrasEditText.setText(it.text)
                scannerView.visibility = View.GONE
                codeScanner.stopPreview()
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            scannerView.post {
                Toast.makeText(scannerView.context, "Error al usar la cÃ¡mara: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        escanearButton.setOnClickListener {
            startScan()
        }

        // ConfiguraciÃ³n del botÃ³n de escanear
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }
    // Verifica permisos y empieza el escaneo
    private fun startScan() {
        if (ContextCompat.checkSelfPermission(scannerView.context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            scannerView.visibility = View.VISIBLE
            codeScanner.startPreview()
        } else {
            ActivityCompat.requestPermissions(
                (scannerView.context as androidx.fragment.app.FragmentActivity),
                arrayOf(Manifest.permission.CAMERA),
                101
            )
        }
    }
    fun stopScanner() {
        codeScanner.releaseResources()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_barcode_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scannerView = view.findViewById<CodeScannerView>(R.id.codeScannerView)
        codeScanner = CodeScanner(requireContext(), scannerView)

        // Verificar si el permiso para la cÃ¡mara estÃ¡ otorgado
        if (requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 123)
        } else {
            codeScanner.startPreview()  // Iniciar la vista previa de la cÃ¡mara
        }

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                // Devolver el resultado del escaneo al fragmento anterior
                val result = it.text
                parentFragmentManager.setFragmentResult("barcode_scanned", bundleOf("barcode" to result))
                parentFragmentManager.popBackStack()
            }
        }

        // Iniciar vista previa de la cÃ¡mara
        codeScanner.startPreview()
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }
}
