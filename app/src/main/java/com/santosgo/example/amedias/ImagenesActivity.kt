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

    private val PICK_IMAGE_REQUEST = 100
    private lateinit var layoutImagenes: LinearLayout
    private lateinit var nickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagenes)

        nickname = intent.getStringExtra("nickname") ?: "Usuario"
        val db = AppDatabase.getDatabase(this)
        val buttonAddImage = findViewById<Button>(R.id.buttonAddImage)
        layoutImagenes = findViewById(R.id.layoutImagenes)

        buttonAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Mostrar imágenes existentes
        val imagenesGuardadas = db.imagenDao().obtenerTodas()
        for (img in imagenesGuardadas) {
            mostrarImagen(Uri.parse(img.uri))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let { uri ->
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                // Guardar en BD
                val db = AppDatabase.getDatabase(this)
                db.imagenDao().insertarImagen(Imagen(uri = uri.toString(), nombreUsuario = nickname))

                mostrarImagen(uri)
                Toast.makeText(this, "Imagen añadida correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarImagen(uri: Uri) {
        val imageView = com.github.chrisbanes.photoview.PhotoView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            ).apply {
                setMargins(0, 16, 0, 16)
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageURI(uri)

            setOnClickListener {
                val intent = Intent(this@ImagenesActivity, FullscreenImageActivity::class.java)
                intent.putExtra("imageUri", uri.toString())
                startActivity(intent)
            }
        }
        layoutImagenes.addView(imageView)
    }
}

