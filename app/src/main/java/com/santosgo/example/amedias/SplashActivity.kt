package com.santosgo.example.amedias

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicia la actividad de login al abrir la app
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))

        // Finaliza la splash activity para que no quede en el historial
        finish()
    }
}

