import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.proyecto.R
class BarcodeScanner : Fragment() {

    private lateinit var codeScanner: CodeScanner

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
