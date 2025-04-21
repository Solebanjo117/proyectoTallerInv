package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.textRegistro).setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<Button>(R.id.btnIngresar).setOnClickListener{
            gotoPrincipal()
        }
    }
    private fun gotoPrincipal(){
        val intent = Intent(this, Principal::class.java)
        startActivity(intent)
        finish()
    }
}