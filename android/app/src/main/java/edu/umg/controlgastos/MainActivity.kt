package edu.umg.controlgastos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.controlgastos.databinding.ActivityMainBinding   // generado por View Binding
import edu.umg.controlgastos.databinding.ItemGastoBinding
import edu.umg.programacion2.gastos.datos.GastoRepositoryEnMemoria  // 👇 todo esto viene del .jar
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.ControlGastos
import edu.umg.programacion2.gastos.servicio.formatearQuetzales
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // lateinit = "la inicializo después, en onCreate". Sin esto Kotlin exige un valor ya.
    private lateinit var binding: ActivityMainBinding

    // IMPORTANTE: la MISMA línea que en el Main.kt de consola. La lógica no cambió.
    private val control = ControlGastos(GastoRepositoryEnMemoria())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Inflar el XML con View Binding y mostrarlo.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. El enum de la librería alimenta el Spinner (muestra el toString(): "Alimentación").
        binding.spCategoria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, Categoria.entries
        )

        // 3. Evento clic: el bloque { } se ejecuta DESPUÉS, cuando el usuario toca el botón.
        binding.btnGuardar.setOnClickListener { guardar() }

        // Botón auxiliar para cargar los mismos gastos de ejemplo del Main.kt de consola.
        binding.btnCargarEjemplos.setOnClickListener { cargarEjemplos() }

        // 4. resumen() es suspend -> se llama dentro de lifecycleScope.launch (receta).
        lifecycleScope.launch { actualizarResumen() }
    }

    private fun guardar() {
        // ❌ Error común: .toDouble() cierra la app si el campo está vacío.
        // ✅ toDoubleOrNull() + early return (null safety de la Clase 11).
        val monto = binding.etMonto.text.toString().toDoubleOrNull()
        if (monto == null) {
            binding.etMonto.error = "Ingresa un monto válido"
            return
        }

        val gasto = Gasto(
            descripcion = binding.etDescripcion.text.toString().trim(),
            monto = monto,
            categoria = binding.spCategoria.selectedItem as Categoria,
            fecha = fechaDeHoy()
        )

        lifecycleScope.launch {
            // 5. La validación vive en la librería, no en la pantalla.
            val error = control.registrar(gasto)
            if (error != null) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                return@launch   // return dentro de launch: sale solo del bloque
            }
            binding.etDescripcion.text.clear()
            binding.etMonto.text.clear()
            Toast.makeText(this@MainActivity, "Gasto registrado correctamente", Toast.LENGTH_SHORT).show()
            actualizarResumen()
        }
    }

    private fun cargarEjemplos() {
        lifecycleScope.launch {
            val hoy = fechaDeHoy()
            control.registrar(
                Gasto(
                    descripcion = "Almuerzo",
                    monto = 45.50,
                    categoria = Categoria.ALIMENTACION,
                    fecha = hoy
                )
            )
            control.registrar(
                Gasto(
                    descripcion = "Bus a la U",
                    monto = 10.00,
                    categoria = Categoria.TRANSPORTE,
                    fecha = hoy
                )
            )
            Toast.makeText(this@MainActivity, "Gastos de ejemplo agregados", Toast.LENGTH_SHORT).show()
            actualizarResumen()
        }
    }

    private fun eliminarGasto(id: Int) {
        lifecycleScope.launch {
            if (control.eliminar(id)) {
                Toast.makeText(this@MainActivity, "Gasto #$id eliminado", Toast.LENGTH_SHORT).show()
                actualizarResumen()
            }
        }
    }

    private suspend fun actualizarResumen() {
        // 1. Resumen principal requerido en Paso 7 ("2 gastos · Total: Q 55.50")
        binding.tvResumen.text = control.resumen()

        // 2. Desglose de totales por categoría usando control.totalPorCategoria()
        val totalesPorCat = control.totalPorCategoria()
        if (totalesPorCat.isEmpty()) {
            binding.tvTotalesCategoria.text = "(Sin gastos registrados por categoría)"
        } else {
            val textoCategorias = StringBuilder()
            for ((categoria, total) in totalesPorCat) {
                if (textoCategorias.isNotEmpty()) textoCategorias.append("\n")
                textoCategorias.append("• $categoria: ${formatearQuetzales(total)}")
            }
            binding.tvTotalesCategoria.text = textoCategorias.toString()
        }

        // 3. Listado de gastos registrados usando control.listar() y opción de eliminar
        val listaGastos = control.listar()
        binding.llListaGastos.removeAllViews()
        if (listaGastos.isEmpty()) {
            binding.tvSinGastos.visibility = View.VISIBLE
        } else {
            binding.tvSinGastos.visibility = View.GONE
            for (gasto in listaGastos) {
                val itemBinding = ItemGastoBinding.inflate(layoutInflater, binding.llListaGastos, false)
                itemBinding.tvItemDescripcion.text = "#${gasto.id} · ${gasto.descripcion}"
                itemBinding.tvItemDetalle.text = "${gasto.categoria} · ${gasto.fecha}"
                itemBinding.tvItemMonto.text = formatearQuetzales(gasto.monto)
                itemBinding.btnEliminarItem.setOnClickListener {
                    eliminarGasto(gasto.id)
                }
                binding.llListaGastos.addView(itemBinding.root)
            }
        }
    }

    // Cuidado: java.time.LocalDate necesita API 26; con minSdk 24 usamos SimpleDateFormat.
    private fun fechaDeHoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

