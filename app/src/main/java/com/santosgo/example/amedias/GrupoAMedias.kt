package com.santosgo.example.amedias

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
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
        val buttonVerBalance = findViewById<Button>(R.id.buttonVerBalance)
        contenedorGastos = findViewById(R.id.contenedorGastos)

        textView.text = "Estás viendo el grupo: $nombreGrupo"

        val db = AppDatabase.getDatabase(this)
        grupoActual = db.grupoDao().obtenerTodos().find { it.nombre == nombreGrupo }

        grupoActual?.let { grupo ->
            val listaGastos = db.gastoDao().obtenerGastosDelGrupo(grupo.id)
            for (g in listaGastos) {
                mostrarGastoEnPantalla(g.nombreUsuario, g.cantidad)
            }
        }

        val buttonEliminarGrupo = findViewById<Button>(R.id.buttonEliminarGrupo)
        if (grupoActual?.creador == nickname) {
            buttonEliminarGrupo.visibility = View.VISIBLE
        }

        buttonAnadirGasto.setOnClickListener {
            mostrarDialogoGasto()
        }

        buttonVerBalance.setOnClickListener {
            val intent = Intent(this, BalanceActivity::class.java)
            intent.putExtra("nombreGrupo", nombreGrupo)
            startActivity(intent)
        }

        buttonEliminarGrupo.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar grupo")
                .setMessage("¿Estás seguro de que quieres eliminar este grupo?")
                .setPositiveButton("Sí") { _, _ ->
                    val db = AppDatabase.getDatabase(this)

                    db.gastoDao().eliminarGastosDeGrupo(grupoActual!!.id)
                    db.usuarioGrupoDao().eliminarRelacionesDeGrupo(grupoActual!!.id)
                    db.grupoDao().eliminarGrupo(grupoActual!!)

                    Toast.makeText(this, "Grupo eliminado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("nickname", nickname)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

    }

    private fun mostrarDialogoGasto() {
        val db = AppDatabase.getDatabase(this)
        val usuariosGrupo = db.usuarioGrupoDao().obtenerUsuariosDeGrupo(grupoActual!!.id)

        val inputCantidad = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Introduce el gasto total"
        }

        val nombres = usuariosGrupo.map { it.nombreUsuario }
        val usuariosSeleccionados = BooleanArray(nombres.size)

        val spinnerPagadoPor = Spinner(this).apply {
            adapter = ArrayAdapter(this@GrupoAMedias, android.R.layout.simple_spinner_dropdown_item, nombres)
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            addView(TextView(this@GrupoAMedias).apply { text = "¿Quién pagó?" })
            addView(spinnerPagadoPor)
            addView(inputCantidad)
        }

        AlertDialog.Builder(this)
            .setTitle("Añadir gasto")
            .setView(layout)
            .setMultiChoiceItems(nombres.toTypedArray(), usuariosSeleccionados) { _, which, isChecked ->
                usuariosSeleccionados[which] = isChecked
            }
            .setPositiveButton("Guardar") { _, _ ->
                val texto = inputCantidad.text.toString()
                val total = texto.toDoubleOrNull()
                val pagadoPor = spinnerPagadoPor.selectedItem.toString()

                if (total != null && total > 0) {
                    val seleccionados = nombres.filterIndexed { i, _ -> usuariosSeleccionados[i] }
                    if (seleccionados.isNotEmpty()) {
                        val cantidadPorPersona = total / seleccionados.size
                        for (usuario in seleccionados) {
                            db.gastoDao().insertarGasto(
                                Gasto(
                                    nombreUsuario = usuario,
                                    idGrupo = grupoActual!!.id,
                                    cantidad = cantidadPorPersona,
                                    pagadoPor = pagadoPor
                                )
                            )
                            mostrarGastoEnPantalla(usuario, cantidadPorPersona)
                        }
                        Toast.makeText(this, "Gasto guardado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Selecciona al menos un usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
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

        val textoGasto = TextView(this).apply {
            text = "Gasto $usuario: %.2f€".format(cantidad)
            textSize = 18f
            setTextColor(resources.getColor(R.color.colorPrimary, null))
        }

        recuadro.addView(textoGasto)
        contenedorGastos.addView(recuadro)
    }
}
