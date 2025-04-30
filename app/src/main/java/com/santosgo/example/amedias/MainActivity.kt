package com.santosgo.example.amedias

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var imageViewProfile: ImageView
    private lateinit var textView: TextView
    private lateinit var buttonLogout: Button

    private val PICK_IMAGE_REQUEST = 1
    private var nickname: String = "Usuario"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        imageViewProfile = findViewById(R.id.imageViewProfile)
        textView = findViewById(R.id.textViewMain)
        buttonLogout = findViewById(R.id.buttonLogout)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Recibir el nombre del usuario
        nickname = intent.getStringExtra("nickname") ?: "Usuario"

        // Mensaje de bienvenida inicial
        textView.text = "Bienvenido $nickname a la app de aMedias"
        buttonLogout.visibility = View.GONE
        imageViewProfile.visibility = View.GONE

        // Clic en la imagen de perfil para abrir galería
        imageViewProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Navegación inferior
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    textView.text = "Bienvenido $nickname a la app de aMedias"
                    buttonLogout.visibility = View.GONE
                    imageViewProfile.visibility = View.GONE
                    true
                }

                R.id.nav_profile -> {
                    textView.text = "$nickname estás en tu perfil"
                    buttonLogout.visibility = View.VISIBLE
                    imageViewProfile.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }

        // Botón de cerrar sesión
        buttonLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    // Resultado de selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                imageViewProfile.setImageURI(selectedImageUri)
            }
        }
    }
}