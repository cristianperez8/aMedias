package com.santosgo.example.amedias

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.santosgo.example.amedias.data.AppDatabase
import com.santosgo.example.amedias.data.UsuarioGrupo

class UnirseGrupoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.unirse_grupo)

        // Referencias a los elementos de la interfaz
        val editTextNombreGrupo = findViewById<EditText>(R.id.editTextNombreGrupo)
        val editTextCreador = findViewById<EditText>(R.id.editTextCreador)
        val buttonUnirse = findViewById<Button>(R.id.buttonUnirse)

        // Obtener el nombre del usuario desde la actividad anterior
        val nickname = intent.getStringExtra("nickname") ?: "Usuario"

        // Acceder a la base de datos local y DAOs necesarios
        val db = AppDatabase.getDatabase(this)
        val grupoDao = db.grupoDao()
        val relacionDao = db.usuarioGrupoDao()

        // Acción al pulsar el botón "Unirse"
        buttonUnirse.setOnClickListener {
            val nombreGrupo = editTextNombreGrupo.text.toString().trim()
            val creador = editTextCreador.text.toString().trim()

            // Validar que ambos campos estén rellenos
            if (nombreGrupo.isNotEmpty() && creador.isNotEmpty()) {
                // Buscar grupo con nombre y creador coincidentes
                val grupo = grupoDao.obtenerTodos().find {
                    it.nombre == nombreGrupo && it.creador == creador
                }

                // Si el grupo existe, crear relación entre usuario y grupo
                if (grupo != null) {
                    relacionDao.unirseAGrupo(UsuarioGrupo(nickname, grupo.id))

                    Toast.makeText(this, "Te has unido al grupo", Toast.LENGTH_SHORT).show()

                    // Volver a la pantalla principal
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("nickname", nickname)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Grupo no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
