package com.example.proyecto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [clientes_registro.newInstance] factory method to
 * create an instance of this fragment.
 */
class clientes_registro : Fragment() {
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
        return inflater.inflate(R.layout.fragment_clientes_registro, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarCliente)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarCliente)
        val nombreInput = view.findViewById<TextInputEditText>(R.id.nombreEditText)
        val telefonoInput = view.findViewById<TextInputEditText>(R.id.telefonoEditText)
        val direccionInput = view.findViewById<TextInputEditText>(R.id.direccionEditText)
        val apellidos = view.findViewById<TextInputEditText>(R.id.apellidoEditText)
        val volverAClientes = {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, clientes()) // <- AquÃ­ va el fragmento original de clientes
                .addToBackStack(null)
                .commit()
        }

        btnGuardar.setOnClickListener {
            val nombre = nombreInput.text.toString().trim() + " " + apellidos.text.toString().trim()
            val telefono = telefonoInput.text.toString().trim()
            val direccion = direccionInput.text.toString().trim()

            if (nombre.isNotEmpty()) {
                val dbHelper = DBHelper(requireContext())
                val db = dbHelper.writableDatabase

                val sql = "INSERT INTO clientes (nombre, telefono, direccion) VALUES (?, ?, ?)"
                val statement = db.compileStatement(sql)
                statement.bindString(1, nombre)
                statement.bindString(2, telefono)
                statement.bindString(3, direccion)
                statement.executeInsert()

                Toast.makeText(requireContext(), "Cliente guardado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            }
            volverAClientes()
        }

        btnCancelar.setOnClickListener {
            volverAClientes()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment clientes_registro.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            clientes_registro().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}