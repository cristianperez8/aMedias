package com.santosgo.example.amedias

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.santosgo.example.amedias.data.AppDatabase
import com.santosgo.example.amedias.data.Grupo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearGrupoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_grupo)

        val editTextNombreGrupo = findViewById<EditText>(R.id.editTextNombreGrupo)
        val buttonConfirmar = findViewById<Button>(R.id.buttonConfirmar)
        val usuarioCreador = intent.getStringExtra("nickname") ?: "Usuario"

        val db = AppDatabase.getDatabase(this)
        val grupoDao = db.grupoDao()

        buttonConfirmar.setOnClickListener {
            val nombreGrupo = editTextNombreGrupo.text.toString().trim()

            if (nombreGrupo.isNotEmpty()) {
                val grupo = Grupo(nombre = nombreGrupo, creador = usuarioCreador)

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        grupoDao.insertarGrupo(grupo)
                    }

                    Toast.makeText(
                        this@CrearGrupoActivity,
                        "Grupo creado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Redirigir a MainActivity con el nombre del grupo
                    val intent = Intent(this@CrearGrupoActivity, MainActivity::class.java)
                    intent.putExtra("nickname", usuarioCreador)
                    intent.putExtra("grupoCreado", nombreGrupo)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Introduce un nombre para el grupo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
