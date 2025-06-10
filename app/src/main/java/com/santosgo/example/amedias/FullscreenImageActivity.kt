package com.santosgo.example.amedias

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.bumptech.glide.request.target.Target

class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        // Referencia al componente PhotoView que permite zoom y desplazamiento en la imagen
        val imageView = findViewById<PhotoView>(R.id.fullscreenImageView)

        // Obtener la URI de la imagen que se pasó desde otra actividad
        val imageUri = intent.getStringExtra("imageUri")

        // Verificar que la URI no sea nula antes de intentar cargar la imagen
        if (imageUri != null) {
            // Usar Glide para cargar la imagen desde la URI y mostrarla en el PhotoView
            Glide.with(this)
                .load(Uri.parse(imageUri)) // Convertir el string a objeto Uri
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Cargar la imagen con su tamaño original
                .into(imageView) // Mostrarla en el PhotoView
        } else {
            // Mostrar mensaje de error si no se recibió ninguna URI válida
            Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
            finish() // Finalizar la actividad ya que no tiene sentido mostrarla sin imagen
        }
    }
}
