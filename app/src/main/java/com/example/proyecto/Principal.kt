package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView

class Principal : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ventas())
                .commit()
            navigationView.setCheckedItem(R.id.idVentas)
            supportActionBar?.title = "Ventas"
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.idVentas -> {
                    supportActionBar?.title = "Ventas"
                    ventas()
                }
                R.id.idInv -> {
                    supportActionBar?.title = "Inventario"
                    inventario() // Instanciamos el fragmento 'inventario'
                }
                R.id.idClientes -> {
                    supportActionBar?.title = "Clientes"
                    clientes()
                }
                R.id.idConsVentas -> {
                    supportActionBar?.title = "Consulta de ventas"
                    consultaVentas()
                }
                R.id.idCerrar -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@setNavigationItemSelectedListener true  // Salimos aqui
                }
                else -> null
            }

            // AquÃ­ reemplazamos el fragmento solo si no es null
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }
}