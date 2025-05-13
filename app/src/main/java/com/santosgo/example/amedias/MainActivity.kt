package com.santosgo.example.amedias

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.santosgo.example.amedias.data.AppDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var imageViewProfile: ImageView
    private lateinit var textView: TextView
    private lateinit var buttonLogout: Button
    private lateinit var buttonContactar: Button
    private lateinit var buttonChangePhoto: Button
    private lateinit var buttonAdd: Button
    private lateinit var buttonCrearGrupo: Button
    private lateinit var buttonUnirseGrupo: Button
    private lateinit var contenedorGrupos: LinearLayout

    private val PICK_IMAGE_REQUEST = 1
    private var nickname: String = "Usuario"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewProfile = findViewById(R.id.imageViewProfile)
        textView = findViewById(R.id.textViewMain)
        buttonLogout = findViewById(R.id.buttonLogout)
        buttonContactar = findViewById(R.id.buttonContactar)
        buttonChangePhoto = findViewById(R.id.buttonChangePhoto)
        buttonAdd = findViewById(R.id.buttonAdd)
        buttonCrearGrupo = findViewById(R.id.buttonCrearGrupo)
        buttonUnirseGrupo = findViewById(R.id.buttonUnirseGrupo)
        contenedorGrupos = findViewById(R.id.contenedorGrupos)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        nickname = intent.getStringExtra("nickname") ?: "Usuario"

        val db = AppDatabase.getDatabase(this)
        val usuario = db.usuarioDao().buscarPorNombre(nickname)

        usuario?.fotoPerfilUri?.let { uriStr ->
            try {
                val uri = Uri.parse(uriStr)
                contentResolver.openInputStream(uri)?.close()
                imageViewProfile.setImageURI(uri)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "No se pudo cargar la imagen de perfil", Toast.LENGTH_SHORT).show()
            }
        }

        textView.text = "Bienvenido $nickname a la app de aMedias"
        buttonLogout.visibility = View.GONE
        buttonContactar.visibility = View.GONE
        imageViewProfile.visibility = View.GONE
        buttonChangePhoto.visibility = View.GONE
        buttonAdd.visibility = View.VISIBLE

        buttonChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Mostrar todos los grupos del usuario actual
        val gruposUsuario = db.grupoDao().obtenerGruposPorUsuario(nickname)

        for (grupo in gruposUsuario) {
            val btnGrupo = Button(this).apply {
                text = grupo.nombre
                textSize = 16f
                setPadding(24, 16, 24, 16)
                setBackgroundResource(R.drawable.fondo_grupo)
                setTextColor(resources.getColor(android.R.color.white, null))

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(0, 0, 0, 24) // margen inferior de 24dp
                this.layoutParams = layoutParams

                setOnClickListener {
                    val intent = Intent(this@MainActivity, GrupoAMedias::class.java)
                    intent.putExtra("nombreGrupo", grupo.nombre)
                    startActivity(intent)
                }
            }
            contenedorGrupos.addView(btnGrupo)

        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    textView.text = "Bienvenido $nickname a la app de aMedias"
                    buttonLogout.visibility = View.GONE
                    buttonContactar.visibility = View.GONE
                    imageViewProfile.visibility = View.GONE
                    buttonChangePhoto.visibility = View.GONE
                    buttonAdd.visibility = View.VISIBLE
                    contenedorGrupos.visibility = View.VISIBLE
                    true
                }

                R.id.nav_profile -> {
                    textView.text = "$nickname estás en tu perfil"
                    buttonLogout.visibility = View.VISIBLE
                    buttonContactar.visibility = View.VISIBLE
                    imageViewProfile.visibility = View.VISIBLE
                    buttonChangePhoto.visibility = View.VISIBLE
                    buttonAdd.visibility = View.GONE
                    buttonCrearGrupo.visibility = View.GONE
                    buttonUnirseGrupo.visibility = View.GONE
                    contenedorGrupos.visibility = View.GONE
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

        buttonContactar.setOnClickListener {
            val nombreApp = "aMedias"
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("amediastfg@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Consulta de la app $nombreApp")
                putExtra(Intent.EXTRA_TEXT, "Hola, soy $nickname. Quisiera hacer una consulta sobre...")
            }

            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(emailIntent)
            } else {
                Toast.makeText(this, "No hay apps de correo instaladas", Toast.LENGTH_SHORT).show()
            }
        }

        buttonAdd.setOnClickListener {
            val visible = buttonCrearGrupo.visibility == View.VISIBLE
            buttonCrearGrupo.visibility = if (visible) View.GONE else View.VISIBLE
            buttonUnirseGrupo.visibility = if (visible) View.GONE else View.VISIBLE
        }

        buttonCrearGrupo.setOnClickListener {
            val intent = Intent(this, CrearGrupoActivity::class.java)
            intent.putExtra("nickname", nickname)
            startActivity(intent)
        }
        buttonUnirseGrupo.setOnClickListener {
            val intent = Intent(this, UnirseGrupoActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        selectedImageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    imageViewProfile.setImageURI(selectedImageUri)
                    val db = AppDatabase.getDatabase(this)
                    db.usuarioDao().actualizarFoto(nickname, selectedImageUri.toString())
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Toast.makeText(this, "No se pudo guardar el permiso de acceso a la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
