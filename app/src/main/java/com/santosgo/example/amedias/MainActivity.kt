package com.santosgo.example.amedias

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val textView = findViewById<TextView>(R.id.textViewMain)
        val buttonLogout = findViewById<Button>(R.id.buttonLogout)

        // Recibir el nombre del usuario
        val nickname = intent.getStringExtra("nickname") ?: "Usuario"

        // Mensaje personalizado en la sección de inicio
        textView.text = "Bienvenido $nickname a la app de aMedias"

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                        textView.text = "Bienvenido $nickname a la APP de aMedias"
                    buttonLogout.visibility = View.GONE
                    true
                }
                R.id.nav_profile -> {
                    textView.text = "Estás en tu perfil"
                    buttonLogout.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }

        buttonLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}


