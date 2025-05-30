package com.santosgo.example.amedias

import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.santosgo.example.amedias.data.AppDatabase

class BalanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance)

        val contenedor = findViewById<LinearLayout>(R.id.contenedorBalance)
        val nombreGrupo = intent.getStringExtra("nombreGrupo") ?: return
        val db = AppDatabase.getDatabase(this)
        val grupo = db.grupoDao().obtenerTodos().find { it.nombre == nombreGrupo } ?: return

        val gastos = db.gastoDao().obtenerGastosDelGrupo(grupo.id)
        val miembros = db.usuarioGrupoDao().obtenerUsuariosDeGrupo(grupo.id).map { it.nombreUsuario }

        val totalGastado = mutableMapOf<String, Double>()
        val totalPagado = mutableMapOf<String, Double>()

        for (usuario in miembros) {
            totalGastado[usuario] = 0.0
            totalPagado[usuario] = 0.0
        }

        for (gasto in gastos) {
            totalGastado[gasto.nombreUsuario] = totalGastado.getOrDefault(gasto.nombreUsuario, 0.0) + gasto.cantidad
            totalPagado[gasto.pagadoPor] = totalPagado.getOrDefault(gasto.pagadoPor, 0.0) + gasto.cantidad
        }

        val balances = mutableMapOf<String, Double>()
        for (usuario in miembros) {
            val pagado = totalPagado[usuario] ?: 0.0
            val gastado = totalGastado[usuario] ?: 0.0
            balances[usuario] = pagado - gastado
        }

        // Mostrar balances individuales
        for ((usuario, balance) in balances) {
            val mensaje = if (balance >= 0) {
                "$usuario debe recibir: %.2f€".format(balance)
            } else {
                "$usuario debe pagar: %.2f€".format(-balance)
            }

            val textView = TextView(this).apply {
                text = mensaje
                textSize = 18f
                typeface = Typeface.SERIF
                setTextColor(resources.getColor(R.color.blueAMedias, null))
                setPadding(0, 8, 0, 0)
            }

            contenedor.addView(textView)
        }

        // Mostrar quién le debe a quién
        val tituloDeudas = TextView(this).apply {
            text = "Deudas entre usuarios:"
            textSize = 22f
            typeface = Typeface.create("serif", Typeface.BOLD)
            setTextColor(resources.getColor(R.color.blueAMedias, null))
            setPadding(0, 48, 0, 8)
        }
        contenedor.addView(tituloDeudas)

        val deudores = balances.filter { it.value < 0 }.toMutableMap()
        val acreedores = balances.filter { it.value > 0 }.toMutableMap()

        for ((deudor, deudaOriginal) in deudores) {
            var deudaRestante = -deudaOriginal
            for ((acreedor, creditoOriginal) in acreedores.toList()) {
                if (deudaRestante <= 0) break
                if (creditoOriginal <= 0) continue

                val cantidad = minOf(deudaRestante, creditoOriginal)
                deudaRestante -= cantidad
                acreedores[acreedor] = creditoOriginal - cantidad

                val textView = TextView(this).apply {
                    text = "$deudor le debe a $acreedor: %.2f€".format(cantidad)
                    textSize = 18f
                    typeface = Typeface.SERIF
                    setPadding(0, 8, 0, 0)
                    setTextColor(resources.getColor(R.color.blueAMedias, null))
                }

                contenedor.addView(textView)
            }
        }

    }
}
