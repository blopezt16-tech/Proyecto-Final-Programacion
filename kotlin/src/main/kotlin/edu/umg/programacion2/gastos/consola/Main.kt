package edu.umg.programacion2.gastos.consola

import edu.umg.programacion2.gastos.datos.GastoRepositoryEnMemoria
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.ControlGastos
import edu.umg.programacion2.gastos.servicio.formatearQuetzales

/**
 * Menu de consola para probar la libreria ANTES de llevarla a Android.
 * Android NO usa este archivo: el telefono tiene su propia "interfaz" (las pantallas).
 *
 * IMPORTANTE: "suspend fun main()" permite llamar funciones suspend directamente desde main.
 * En Android no existe main(); alli se usa lifecycleScope.launch { ... }.
 */
suspend fun main() {
    // 1. Armamos las piezas: el control recibe el repositorio que queremos usar.
    val control = ControlGastos(GastoRepositoryEnMemoria())

    // 2. Datos de ejemplo para no empezar con la lista vacia.
    control.registrar(Gasto(descripcion = "Almuerzo", monto = 45.50, categoria = Categoria.ALIMENTACION, fecha = "2026-10-03"))
    control.registrar(Gasto(descripcion = "Bus a la U", monto = 10.0, categoria = Categoria.TRANSPORTE, fecha = "2026-10-03"))

    // 3. Ciclo del menu: se repite hasta que el usuario elige 0.
    while (true) {
        println("\n=== Control de Gastos (${control.resumen()}) ===")
        println("1. Registrar gasto")
        println("2. Listar gastos")
        println("3. Total por categoria")
        println("4. Eliminar gasto")
        println("0. Salir")
        print("Opcion: ")

        when (readlnOrNull()?.trim()) {
            "1" -> registrarDesdeConsola(control)
            "2" -> listar(control)
            "3" -> mostrarTotalesPorCategoria(control)
            "4" -> eliminarDesdeConsola(control)
            "0", null -> {  // null = se cerro la entrada (Ctrl+D)
                println("Hasta pronto")
                return
            }
            else -> println("Opcion no valida")
        }
    }
}

private suspend fun registrarDesdeConsola(control: ControlGastos) {
    print("Descripcion: ")
    val descripcion = readlnOrNull()?.trim() ?: ""

    // Cuidado: toDouble() lanzaria una excepcion con "abc" o con texto vacio.
    // toDoubleOrNull() devuelve null y nosotros decidimos que hacer (Clase 11, null safety).
    print("Monto: ")
    val monto = readlnOrNull()?.trim()?.toDoubleOrNull()
    if (monto == null) {
        println("ERROR -> El monto debe ser un numero")
        return
    }

    val categoria = elegirCategoria() ?: return

    print("Fecha (yyyy-MM-dd): ")
    val fecha = readlnOrNull()?.trim() ?: ""

    val error = control.registrar(Gasto(descripcion = descripcion, monto = monto, categoria = categoria, fecha = fecha))
    if (error == null) {
        println("OK    -> Gasto registrado")
    } else {
        println("ERROR -> $error")
    }
}

/** Muestra las categorias numeradas y devuelve la elegida, o null si la opcion no existe. */
private fun elegirCategoria(): Categoria? {
    // Categoria.entries = lista con todos los valores del enum, en el orden en que se declararon.
    val categorias = Categoria.entries
    for ((indice, categoria) in categorias.withIndex()) {
        println("  ${indice + 1}. $categoria")
    }
    print("Categoria: ")
    val numero = readlnOrNull()?.trim()?.toIntOrNull()
    if (numero == null || numero !in 1..categorias.size) {
        println("ERROR -> Categoria no valida")
        return null
    }
    return categorias[numero - 1]
}

private suspend fun listar(control: ControlGastos) {
    val gastos = control.listar()
    if (gastos.isEmpty()) {
        println("(no hay gastos)")
        return
    }
    for (gasto in gastos) {
        println("#${gasto.id}  ${gasto.fecha}  ${gasto.descripcion} (${gasto.categoria})  ${formatearQuetzales(gasto.monto)}")
    }
}

private suspend fun mostrarTotalesPorCategoria(control: ControlGastos) {
    val totales = control.totalPorCategoria()
    if (totales.isEmpty()) {
        println("(no hay gastos)")
        return
    }
    // Recorrer un Map entrega pares (llave, valor).
    for ((categoria, total) in totales) {
        println("$categoria: ${formatearQuetzales(total)}")
    }
}

private suspend fun eliminarDesdeConsola(control: ControlGastos) {
    print("Id a eliminar: ")
    val id = readlnOrNull()?.trim()?.toIntOrNull()
    if (id == null) {
        println("ERROR -> El id debe ser un numero")
        return
    }
    if (control.eliminar(id)) {
        println("OK    -> Gasto #$id eliminado")
    } else {
        println("ERROR -> No existe el gasto #$id")
    }
}
