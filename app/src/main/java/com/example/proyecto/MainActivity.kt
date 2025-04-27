package com.example.proyecto

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext


class MainActivity : AppCompatActivity() {
    private lateinit var txtUsuario: EditText
    private lateinit var txtContra: EditText
    private lateinit var btnIngresar: Button
    private lateinit var textRegistro: TextView
    private lateinit var db: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtUsuario = findViewById(R.id.txtUsuario);
        txtContra = findViewById(R.id.txtContra);
        btnIngresar = findViewById(R.id.btnIngresar);
        textRegistro = findViewById(R.id.textRegistro);
        val dbHelper = DBHelper(this)
        // Inicializar la base de datos
        db = dbHelper.readableDatabase

        btnIngresar.setOnClickListener(View.OnClickListener { v: View? -> loginUser() })

        findViewById<TextView>(R.id.textRegistro).setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<Button>(R.id.btnIngresar).setOnClickListener{
            loginUser()
        }
    }
    private fun gotoPrincipal(){
        val intent = Intent(this, Principal::class.java)
        startActivity(intent)
        finish()
    }
    private fun loginUser() {
        val usuario = txtUsuario.text.toString().trim()
        val contrasena = txtContra.text.toString().trim()

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?",
            arrayOf(usuario, contrasena)
        )

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show()
            gotoPrincipal()
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
    }
}