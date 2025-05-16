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

        val imageView = findViewById<PhotoView>(R.id.fullscreenImageView)
        val imageUri = intent.getStringExtra("imageUri")

        if (imageUri != null) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imageView)
        } else {
            Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
