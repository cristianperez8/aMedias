package com.santosgo.example.amedias

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.santosgo.example.amedias.data.AppDatabase
import com.santosgo.example.amedias.data.Gasto
import com.santosgo.example.amedias.data.Grupo

class GrupoAMedias : AppCompatActivity() {

    private lateinit var contenedorGastos: LinearLayout
    private lateinit var nombreGrupo: String
    private lateinit var nickname: String
    private var grupoActual: Grupo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupoamedias)

        nombreGrupo = intent.getStringExtra("nombreGrupo") ?: "Grupo"
        nickname = intent.getStringExtra("nickname") ?: "Usuario"

        val textView = findViewById<TextView>(R.id.textViewDetalleGrupo)
        val buttonAnadirGasto = findViewById<Button>(R.id.buttonAnadirGasto)
        contenedorGastos = findViewById(R.id.contenedorGastos)

        textView.text = "Estás viendo el grupo: $nombreGrupo"

        val db = AppDatabase.getDatabase(this)
        grupoActual = db.grupoDao().obtenerTodos().find { it.nombre == nombreGrupo }

        grupoActual?.let { grupo ->
            // Mostrar los gastos existentes
            val listaGastos = db.gastoDao().obtenerGastosDelGrupo(grupo.id)
            for (g in listaGastos) {
                mostrarGastoEnPantalla(g.nombreUsuario, g.cantidad)
            }
        }

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
                val cantidad = texto.toDoubleOrNull()
                if (cantidad != null && cantidad > 0 && grupoActual != null) {
                    val db = AppDatabase.getDatabase(this)
                    val gasto = Gasto(nombreUsuario = nickname, idGrupo = grupoActual!!.id, cantidad = cantidad)
                    db.gastoDao().insertarGasto(gasto)

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
