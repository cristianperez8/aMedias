package com.santosgo.example.amedias

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class GrupoAMedias : AppCompatActivity() {

    private lateinit var contenedorGastos: LinearLayout
    private lateinit var nombreGrupo: String
    private lateinit var nickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupoamedias)

        nombreGrupo = intent.getStringExtra("nombreGrupo") ?: "Grupo"
        nickname = intent.getStringExtra("nickname") ?: "Usuario"

        val textView = findViewById<TextView>(R.id.textViewDetalleGrupo)
        val buttonAnadirGasto = findViewById<Button>(R.id.buttonAnadirGasto)
        contenedorGastos = findViewById(R.id.contenedorGastos)

        textView.text = "Estás viendo el grupo: $nombreGrupo"

        buttonAnadirGasto.setOnClickListener {
            mostrarDialogoGasto()
        }
    }

    private fun mostrarDialogoGasto() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Introduce el gasto"
        }

        AlertDialog.Builder(this)
            .setTitle("Añadir gasto")
            .setMessage("¿Cuánto se ha gastado?")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val texto = input.text.toString()
                val cantidad = texto.toDouble()
                if (cantidad != null && cantidad > 0) {
                    mostrarGastoEnPantalla(nickname, cantidad)
                    Toast.makeText(this, "Gasto de %.2f€ registrado".format(cantidad), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Introduce un número válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarGastoEnPantalla(usuario: String, cantidad: Double) {
        val recuadro = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.fondo_grupo)
            setPadding(32, 24, 32, 24)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 24)
            layoutParams = params
        }

        val textoUsuario = TextView(this).apply {
            text = "Registrado por: $usuario"
            textSize = 16f
            setTextColor(resources.getColor(R.color.colorPrimary, null))
        }

        val textoCantidad = TextView(this).apply {
            text = "Gasto: %.2f€".format(cantidad)
            textSize = 18f
            setTextColor(resources.getColor(R.color.colorPrimary, null))
        }

        recuadro.addView(textoUsuario)
        recuadro.addView(textoCantidad)

        contenedorGastos.addView(recuadro)
    }
}
