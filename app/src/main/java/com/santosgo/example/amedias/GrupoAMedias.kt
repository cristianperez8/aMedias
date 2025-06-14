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
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class GrupoAMedias : AppCompatActivity() {

    private lateinit var contenedorGastos: LinearLayout
    private lateinit var nombreGrupo: String
    private lateinit var nickname: String
    private var grupoActual: Grupo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupoamedias)

        // Obtener datos de la intención
        nombreGrupo = intent.getStringExtra("nombreGrupo") ?: "Grupo"
        nickname = intent.getStringExtra("nickname") ?: "Usuario"

        // Referencias a vistas del layout
        val textView = findViewById<TextView>(R.id.textViewDetalleGrupo)
        val buttonAnadirGasto = findViewById<Button>(R.id.buttonAnadirGasto)
        val buttonVerBalance = findViewById<Button>(R.id.buttonVerBalance)
        val buttonEliminarGrupo = findViewById<Button>(R.id.buttonEliminarGrupo)
        val buttonSalirGrupo = findViewById<Button>(R.id.buttonSalirGrupo)
        contenedorGastos = findViewById(R.id.contenedorGastos)

        // Mostrar nombre del grupo
        textView.text = "Estás viendo el grupo: $nombreGrupo"

        val db = AppDatabase.getDatabase(this)
        grupoActual = db.grupoDao().obtenerTodos().find { it.nombre == nombreGrupo }

        grupoActual?.let { grupo ->
            val listaGastos = db.gastoDao().obtenerGastosDelGrupo(grupo.id)
            for (g in listaGastos) {
                mostrarGastoEnPantalla(g.nombreUsuario, g.cantidad)
            }

            // Mostrar botones según si el usuario es el creador o no
            if (grupo.creador == nickname) {
                buttonEliminarGrupo.visibility = View.VISIBLE
                buttonSalirGrupo.visibility = View.GONE
            } else {
                buttonEliminarGrupo.visibility = View.GONE
                buttonSalirGrupo.visibility = View.VISIBLE
            }

            // Salir del grupo solo si el balance del usuario es 0€
            buttonSalirGrupo.setOnClickListener {
                val gastos = db.gastoDao().obtenerGastosDelGrupo(grupo.id)
                val totalPagado = gastos.filter { it.pagadoPor == nickname }.sumOf { it.cantidad }
                val totalDeuda = gastos.filter { it.nombreUsuario == nickname }.sumOf { it.cantidad }
                val balance = totalPagado - totalDeuda

                if (balance == 0.0) {
                    db.usuarioGrupoDao().eliminarUsuarioDelGrupo(nickname, grupo.id)
                    Toast.makeText(this, "Has salido del grupo", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java).putExtra("nickname", nickname))
                    finish()
                } else {
                    Toast.makeText(this, "Tu balance debe ser 0€ para salir", Toast.LENGTH_LONG).show()
                }
            }

            // Eliminar el grupo si todos los usuarios tienen balance 0
            buttonEliminarGrupo.setOnClickListener {
                val usuariosGrupo = db.usuarioGrupoDao().obtenerUsuariosDeGrupo(grupo.id)
                val gastos = db.gastoDao().obtenerGastosDelGrupo(grupo.id)

                val balances = usuariosGrupo.map { usuario ->
                    val totalPagado = gastos.filter { it.pagadoPor == usuario.nombreUsuario }.sumOf { it.cantidad }
                    val totalDeuda = gastos.filter { it.nombreUsuario == usuario.nombreUsuario }.sumOf { it.cantidad }
                    usuario.nombreUsuario to (totalPagado - totalDeuda)
                }

                val usuariosConDeuda = balances.filter { it.second != 0.0 }

                if (usuariosConDeuda.isEmpty()) {
                    AlertDialog.Builder(this)
                        .setTitle("Eliminar grupo")
                        .setMessage("¿Estás seguro de que quieres eliminar este grupo?")
                        .setPositiveButton("Sí") { _, _ ->
                            db.gastoDao().eliminarGastosDeGrupo(grupo.id)
                            db.usuarioGrupoDao().eliminarRelacionesDeGrupo(grupo.id)
                            db.grupoDao().eliminarGrupo(grupo)
                            Toast.makeText(this, "Grupo eliminado", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java).putExtra("nickname", nickname))
                            finish()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                } else {
                    val mensaje = usuariosConDeuda.joinToString("\n") { (usuario, balance) ->
                        "• $usuario: ${"%.2f".format(balance)}€"
                    }

                    AlertDialog.Builder(this)
                        .setTitle("No se puede eliminar el grupo")
                        .setMessage("Los siguientes usuarios no tienen balance 0:\n\n$mensaje")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }

        // Añadir gasto
        buttonAnadirGasto.setOnClickListener { mostrarDialogoGasto() }

        // Ver balance del grupo
        buttonVerBalance.setOnClickListener {
            startActivity(Intent(this, BalanceActivity::class.java).putExtra("nombreGrupo", nombreGrupo))
        }

        // Ver fotos del grupo
        findViewById<Button>(R.id.buttonFotos).setOnClickListener {
            startActivity(Intent(this, ImagenesActivity::class.java).apply {
                putExtra("nickname", nickname)
                putExtra("idGrupo", grupoActual?.id ?: 0)
            })
        }

        // Exportar los gastos del grupo a PDF
        findViewById<Button>(R.id.buttonExportarPDF).setOnClickListener {
            exportarGastosAPDF()
        }
    }

    // Muestra un diálogo para introducir un nuevo gasto y repartirlo entre usuarios
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
                val total = inputCantidad.text.toString().toDoubleOrNull()
                val pagadoPor = spinnerPagadoPor.selectedItem.toString()
                val seleccionados = nombres.filterIndexed { i, _ -> usuariosSeleccionados[i] }

                if (total != null && total > 0 && seleccionados.isNotEmpty()) {
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
                    Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Muestra visualmente un gasto en pantalla dentro de un recuadro
    private fun mostrarGastoEnPantalla(usuario: String, cantidad: Double) {
        val recuadro = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.fondo_grupo)
            setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 24) }
        }

        val textoGasto = TextView(this).apply {
            text = "Gasto $usuario: %.2f€".format(cantidad)
            textSize = 18f
            setTextColor(resources.getColor(R.color.colorPrimary, null))
        }

        recuadro.addView(textoGasto)
        contenedorGastos.addView(recuadro)
    }

    // Exporta los gastos del grupo a un documento PDF y lo guarda en la carpeta Descargas
    private fun exportarGastosAPDF() {
        val db = AppDatabase.getDatabase(this)
        val gastos = db.gastoDao().obtenerGastosDelGrupo(grupoActual?.id ?: return)

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        paint.textSize = 12f
        var y = 25f
        canvas.drawText("Gastos del grupo: $nombreGrupo", 10f, y, paint)
        y += 20f

        for (gasto in gastos) {
            val linea = "Gasto ${gasto.nombreUsuario}: %.2f€".format(gasto.cantidad)
            canvas.drawText(linea, 10f, y, paint)
            y += 20f
            if (y > 580f) break // evitar que el contenido se desborde de la página
        }

        pdfDocument.finishPage(page)

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, "Gastos_${nombreGrupo}.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF guardado", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Error al guardar PDF", Toast.LENGTH_LONG).show()
        }

        pdfDocument.close()
    }
}