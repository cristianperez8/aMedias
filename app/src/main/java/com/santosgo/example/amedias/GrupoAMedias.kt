package com.santosgo.example.amedias

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GrupoAMedias : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupoamedias)

        val nombreGrupo = intent.getStringExtra("nombreGrupo") ?: "Grupo"
        val textView = findViewById<TextView>(R.id.textViewDetalleGrupo)
        textView.text = "Estás viendo el grupo: $nombreGrupo"
    }
}
