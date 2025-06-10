package com.santosgo.example.amedias

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.santosgo.example.amedias.data.AppDatabase
import com.santosgo.example.amedias.data.Usuario

class LoginActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Obtener instancia de la base de datos
        db = AppDatabase.getDatabase(this)

        // Referencias a los elementos del layout
        val nicknameEditText = findViewById<EditText>(R.id.editTextNickname)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        // Acción del botón "Iniciar sesión"
        loginButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar que los campos no estén vacíos
            if (nickname.isNotEmpty() && password.isNotEmpty()) {
                // Consultar en la base de datos si existe el usuario con esas credenciales
                val usuario = db.usuarioDao().login(nickname, password)
                if (usuario != null) {
                    // Si existe, pasar a la actividad principal
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("nickname", usuario.nombreUsuario)
                    startActivity(intent)
                    finish()
                } else {
                    // Si las credenciales no coinciden
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción del botón "Registrarse"
        registerButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar que no estén vacíos
            if (nickname.isNotEmpty() && password.isNotEmpty()) {
                // Comprobar si el usuario ya existe
                val existe = db.usuarioDao().buscarPorNombre(nickname)
                if (existe != null) {
                    Toast.makeText(this, "El usuario ya existe, elige otro", Toast.LENGTH_SHORT).show()
                } else {
                    // Crear nuevo usuario y guardarlo en la base de datos
                    val nuevoUsuario = Usuario(nombreUsuario = nickname, contrasena = password)
                    db.usuarioDao().insertar(nuevoUsuario)
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
