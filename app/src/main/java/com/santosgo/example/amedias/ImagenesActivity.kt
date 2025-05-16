package com.santosgo.example.amedias

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ImagenesActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 100
    private lateinit var layoutImagenes: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagenes)

        val buttonAddImage = findViewById<Button>(R.id.buttonAddImage)
        layoutImagenes = findViewById(R.id.layoutImagenes)

        buttonAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500).apply {
                        setMargins(0, 16, 0, 16)
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageURI(it)
                }

                layoutImagenes.addView(imageView)
                Toast.makeText(this, "Imagen añadida correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
