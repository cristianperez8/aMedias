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

        db = AppDatabase.getDatabase(this)

        val nicknameEditText = findViewById<EditText>(R.id.editTextNickname)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        loginButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (nickname.isNotEmpty() && password.isNotEmpty()) {
                val usuario = db.usuarioDao().login(nickname, password)
                if (usuario != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("nickname", usuario.nombreUsuario)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (nickname.isNotEmpty() && password.isNotEmpty()) {
                val existe = db.usuarioDao().buscarPorNombre(nickname)
                if (existe != null) {
                    Toast.makeText(this, "El usuario ya existe, elige otro", Toast.LENGTH_SHORT).show()
                } else {
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
