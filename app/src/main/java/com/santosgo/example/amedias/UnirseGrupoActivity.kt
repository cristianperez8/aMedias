package com.santosgo.example.amedias

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.santosgo.example.amedias.data.AppDatabase

class UnirseGrupoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.unirse_grupo)

        val editTextNombreGrupo = findViewById<EditText>(R.id.editTextNombreGrupo)
        val editTextCreador = findViewById<EditText>(R.id.editTextCreador)
        val buttonUnirse = findViewById<Button>(R.id.buttonUnirse)

        val db = AppDatabase.getDatabase(this)

        buttonUnirse.setOnClickListener {
            val nombreGrupo = editTextNombreGrupo.text.toString().trim()
            val creador = editTextCreador.text.toString().trim()

            if (nombreGrupo.isNotEmpty() && creador.isNotEmpty()) {
                val grupo = db.grupoDao().obtenerTodos().find {
                    it.nombre == nombreGrupo && it.creador == creador
                }

                if (grupo != null) {
                    val intent = Intent(this, GrupoAMedias::class.java)
                    intent.putExtra("nombreGrupo", grupo.nombre)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Grupo no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
