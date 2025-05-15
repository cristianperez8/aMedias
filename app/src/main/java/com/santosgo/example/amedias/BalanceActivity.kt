package com.santosgo.example.amedias

import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.santosgo.example.amedias.data.AppDatabase
import com.santosgo.example.amedias.data.Grupo

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
                setPadding(0, 16, 0, 0)
            }

            contenedor.addView(textView)
        }
    }
}