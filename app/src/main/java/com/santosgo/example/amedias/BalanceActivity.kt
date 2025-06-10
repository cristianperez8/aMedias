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

        // Referencia al contenedor en el layout donde se mostrarán los balances
        val contenedor = findViewById<LinearLayout>(R.id.contenedorBalance)

        // Obtener el nombre del grupo pasado por la intención (intent)
        val nombreGrupo = intent.getStringExtra("nombreGrupo") ?: return

        // Obtener instancia de la base de datos
        val db = AppDatabase.getDatabase(this)

        // Buscar el grupo en la base de datos usando el nombre
        val grupo = db.grupoDao().obtenerTodos().find { it.nombre == nombreGrupo } ?: return

        // Obtener los gastos y miembros del grupo desde la base de datos
        val gastos = db.gastoDao().obtenerGastosDelGrupo(grupo.id)
        val miembros = db.usuarioGrupoDao().obtenerUsuariosDeGrupo(grupo.id).map { it.nombreUsuario }

        // Inicializar mapas para guardar el total gastado y pagado por cada usuario
        val totalGastado = mutableMapOf<String, Double>()
        val totalPagado = mutableMapOf<String, Double>()

        // Inicializar valores a 0 para cada miembro
        for (usuario in miembros) {
            totalGastado[usuario] = 0.0
            totalPagado[usuario] = 0.0
        }

        // Acumular los gastos: quién lo generó (nombreUsuario) y quién lo pagó (pagadoPor)
        for (gasto in gastos) {
            totalGastado[gasto.nombreUsuario] = totalGastado.getOrDefault(gasto.nombreUsuario, 0.0) + gasto.cantidad
            totalPagado[gasto.pagadoPor] = totalPagado.getOrDefault(gasto.pagadoPor, 0.0) + gasto.cantidad
        }

        // Calcular balance individual: lo pagado menos lo gastado
        val balances = mutableMapOf<String, Double>()
        for (usuario in miembros) {
            val pagado = totalPagado[usuario] ?: 0.0
            val gastado = totalGastado[usuario] ?: 0.0
            balances[usuario] = pagado - gastado
        }

        // Mostrar el balance de cada usuario en pantalla
        for ((usuario, balance) in balances) {
            val mensaje = if (balance >= 0) {
                "$usuario debe recibir: %.2f€".format(balance)
            } else {
                "$usuario debe pagar: %.2f€".format(-balance)
            }

            // Crear un TextView dinámicamente para mostrar el mensaje
            val textView = TextView(this).apply {
                text = mensaje
                textSize = 18f
                typeface = Typeface.SERIF
                setTextColor(resources.getColor(R.color.blueAMedias, null))
                setPadding(0, 8, 0, 0)
            }

            contenedor.addView(textView)
        }

        // Título para la sección de deudas entre usuarios
        val tituloDeudas = TextView(this).apply {
            text = "Deudas entre usuarios:"
            textSize = 22f
            typeface = Typeface.create("serif", Typeface.BOLD)
            setTextColor(resources.getColor(R.color.blueAMedias, null))
            setPadding(0, 48, 0, 8)
        }
        contenedor.addView(tituloDeudas)

        // Separar usuarios en deudores y acreedores (los que tienen balance negativo o positivo)
        val deudores = balances.filter { it.value < 0 }.toMutableMap()
        val acreedores = balances.filter { it.value > 0 }.toMutableMap()

        // Resolver las deudas entre deudores y acreedores
        for ((deudor, deudaOriginal) in deudores) {
            var deudaRestante = -deudaOriginal
            for ((acreedor, creditoOriginal) in acreedores.toList()) {
                if (deudaRestante <= 0) break
                if (creditoOriginal <= 0) continue

                // Calcular cuánto puede pagar el deudor al acreedor
                val cantidad = minOf(deudaRestante, creditoOriginal)
                deudaRestante -= cantidad
                acreedores[acreedor] = creditoOriginal - cantidad

                // Mostrar la deuda entre los usuarios
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
