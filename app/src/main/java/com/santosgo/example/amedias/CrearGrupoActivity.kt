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
import com.santosgo.example.amedias.data.UsuarioGrupo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearGrupoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_grupo)

        // Referencia al campo de texto donde se introduce el nombre del grupo
        val editTextNombreGrupo = findViewById<EditText>(R.id.editTextNombreGrupo)

        // Botón para confirmar la creación del grupo
        val buttonConfirmar = findViewById<Button>(R.id.buttonConfirmar)

        // Obtener el nombre del usuario que crea el grupo desde la actividad anterior
        val usuarioCreador = intent.getStringExtra("nickname") ?: "Usuario"

        // Inicializar la base de datos y los DAOs correspondientes
        val db = AppDatabase.getDatabase(this)
        val grupoDao = db.grupoDao()
        val usuarioGrupoDao = db.usuarioGrupoDao()

        // Configurar la acción al pulsar el botón de confirmar
        buttonConfirmar.setOnClickListener {
            val nombreGrupo = editTextNombreGrupo.text.toString().trim()

            // Validar que el nombre del grupo no esté vacío
            if (nombreGrupo.isNotEmpty()) {
                // Crear objeto Grupo con el nombre y el creador
                val grupo = Grupo(nombre = nombreGrupo, creador = usuarioCreador)

                // Usar corrutinas para operaciones en segundo plano (base de datos)
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        // Insertar el grupo en la base de datos
                        grupoDao.insertarGrupo(grupo)

                        // Recuperar el grupo recién creado
                        val grupoCreado = grupoDao.obtenerTodos().find {
                            it.nombre == nombreGrupo && it.creador == usuarioCreador
                        }

                        // Registrar al creador como miembro del grupo
                        grupoCreado?.let {
                            usuarioGrupoDao.unirseAGrupo(
                                UsuarioGrupo(usuarioCreador, it.id)
                            )
                        }
                    }

                    // Mostrar mensaje de éxito al usuario
                    Toast.makeText(
                        this@CrearGrupoActivity,
                        "Grupo creado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Volver a la pantalla principal (MainActivity), pasando el nickname y nombre del grupo
                    val intent = Intent(this@CrearGrupoActivity, MainActivity::class.java)
                    intent.putExtra("nickname", usuarioCreador)
                    intent.putExtra("grupoCreado", nombreGrupo)
                    startActivity(intent)

                    // Finalizar esta actividad para no volver con el botón atrás
                    finish()
                }
            } else {
                // Mostrar error si el campo está vacío
                Toast.makeText(this, "Introduce un nombre para el grupo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
