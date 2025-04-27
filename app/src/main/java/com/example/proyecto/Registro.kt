package com.example.proyecto

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Registro : AppCompatActivity() {
    private lateinit var editTextUsuario: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextPassword2: EditText
    private lateinit var buttonRegistrarse: Button
    private lateinit var btnVolverLogin: Button
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro) // Aquí enlazas tu layout
        editTextUsuario = findViewById(R.id.editTextText)
        editTextPassword = findViewById(R.id.editTextTextPassword)
        editTextPassword2 = findViewById(R.id.editTextTextPassword2)
        buttonRegistrarse = findViewById(R.id.button)
        btnVolverLogin = findViewById(R.id.btnVolverLogin)
        var dbHelper =DBHelper(this)
        db = dbHelper.writableDatabase
        findViewById<Button>(R.id.btnVolverLogin).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<Button>(R.id.button).setOnClickListener {
            registerUser()
        }
    }
    private fun registerUser() {
        val usuario = editTextUsuario.text.toString().trim()
        val contrasena = editTextPassword.text.toString().trim()
        val confirmarContrasena = editTextPassword2.text.toString().trim()

        if (usuario.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (contrasena != confirmarContrasena) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        val values = ContentValues().apply {
            put("usuario", usuario)
            put("contrasena", contrasena)
        }

        try {
            val resultado = db.insertOrThrow("usuarios", null, values)
            if (resultado != -1L) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: SQLiteConstraintException) {
            Toast.makeText(this, "Este usuario ya existe", Toast.LENGTH_SHORT).show()
        }
    }
}