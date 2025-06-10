package com.santosgo.example.amedias

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.santosgo.example.amedias.data.AppDatabase
import com.santosgo.example.amedias.data.Imagen

class ImagenesActivity : AppCompatActivity() {

    // Código de solicitud para elegir una imagen
    private val PICK_IMAGE_REQUEST = 100

    // Elementos de la interfaz y datos del grupo
    private lateinit var layoutImagenes: LinearLayout
    private lateinit var nickname: String
    private var idGrupo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagenes)

        // Obtener datos del intent (usuario y grupo)
        nickname = intent.getStringExtra("nickname") ?: "Usuario"
        idGrupo = intent.getIntExtra("idGrupo", 0)

        // Inicializar base de datos y vistas
        val db = AppDatabase.getDatabase(this)
        val buttonAddImage = findViewById<Button>(R.id.buttonAddImage)
        layoutImagenes = findViewById(R.id.layoutImagenes)

        // Acción del botón para añadir nueva imagen
        buttonAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*" // Solo imágenes
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Cargar imágenes ya guardadas en base de datos
        val imagenesDelGrupo = db.imagenDao().obtenerPorGrupo(idGrupo)
        for (img in imagenesDelGrupo) {
            mostrarImagen(Uri.parse(img.uri))
        }
    }

    // Método que se ejecuta al volver del selector de imágenes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verificar que la imagen fue seleccionada correctamente
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let { uri ->
                // Conservar el permiso de lectura de la URI
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                // Guardar imagen en la base de datos
                val db = AppDatabase.getDatabase(this)
                db.imagenDao().insertarImagen(
                    Imagen(uri = uri.toString(), nombreUsuario = nickname, idGrupo = idGrupo)
                )

                // Mostrar visualmente la imagen
                mostrarImagen(uri)
                Toast.makeText(this, "Imagen añadida correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para visualizar una imagen en el LinearLayout usando PhotoView
    private fun mostrarImagen(uri: Uri) {
        val imageView = PhotoView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600 // Altura fija
            ).apply {
                setMargins(0, 16, 0, 16)
            }

            // Estilo y comportamiento
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageURI(uri)

            // Al hacer clic en la imagen, abrirla en pantalla completa
            setOnClickListener {
                val intent = Intent(this@ImagenesActivity, FullscreenImageActivity::class.java)
                intent.putExtra("imageUri", uri.toString())
                startActivity(intent)
            }
        }

        // Añadir la imagen al layout principal
        layoutImagenes.addView(imageView)
    }
}
