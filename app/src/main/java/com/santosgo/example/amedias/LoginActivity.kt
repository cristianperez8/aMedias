package com.santosgo.example.amedias

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val nicknameEditText = findViewById<EditText>(R.id.editTextNickname)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (nickname.isNotEmpty() && password.isNotEmpty()) {
                // Modificar este bloque para la base de datos en la nube
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("nickname", nickname)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
