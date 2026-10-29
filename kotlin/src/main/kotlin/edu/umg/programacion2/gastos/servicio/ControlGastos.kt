package edu.umg.programacion2.gastos.servicio

import edu.umg.programacion2.gastos.datos.GastoRepository
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto

/**
 * Las reglas del negocio: validar, registrar y calcular totales.
 *
 * IMPORTANTE: recibe el repositorio en el constructor y solo conoce la INTERFACE.
 * Por eso funciona igual con la lista en memoria (hoy) que con Room (semana 4):
 *
 *   val control = ControlGastos(GastoRepositoryEnMemoria())   // hoy
 *   val control = ControlGastos(GastoRepositoryRoom(dao))      // semana 4
 *
 * Esta clase NO sabe nada de pantallas: no hay println ni Toast. Devuelve datos y
 * mensajes, y cada interfaz (consola o Android) decide como mostrarlos.
 */
class ControlGastos(private val repositorio: GastoRepository) {

    /**
     * Revisa las reglas del negocio.
     * Devuelve null si el gasto es valido, o el mensaje del PRIMER error encontrado.
     */
    fun validar(gasto: Gasto): String? {
        // Early returns: en cuanto hay un problema, lo devolvemos.
        if (gasto.descripcion.isBlank()) {
            return "La descripción no puede estar vacía"
        }
        if (gasto.monto <= 0) {
            return "El monto debe ser mayor que 0"
        }
        if (!FORMATO_FECHA.matches(gasto.fecha)) {
            return "La fecha debe tener el formato yyyy-MM-dd"
        }
        return null
    }

    /**
     * Valida y, si todo esta bien, guarda el gasto.
     * Devuelve null si se guardo, o el mensaje de error si no.
     * (Mismo estilo que GestorEstudiantes.agregar() de la Clase 11.)
     */
    suspend fun registrar(gasto: Gasto): String? {
        val error = validar(gasto)
        if (error != null) {
            return error
        }
        repositorio.agregar(gasto)
        return null
    }

    suspend fun listar(): List<Gasto> = repositorio.listar()

    suspend fun eliminar(id: Int): Boolean = repositorio.eliminar(id)

    suspend fun total(): Double {
        var suma = 0.0
        for (gasto in repositorio.listar()) {
            suma += gasto.monto
        }
        return suma
    }

    /**
     * Agrupa los montos por categoria usando un Map (Clase de colecciones).
     * Solo aparecen las categorias que tienen al menos un gasto.
     */
    suspend fun totalPorCategoria(): Map<Categoria, Double> {
        val totales = mutableMapOf<Categoria, Double>()
        for (gasto in repositorio.listar()) {
            // Cuidado: totales[categoria] devuelve Double? (null si aun no existe la llave).
            // El operador ?: nos da 0.0 en ese caso.
            val acumulado = totales[gasto.categoria] ?: 0.0
            totales[gasto.categoria] = acumulado + gasto.monto
        }
        return totales
    }

    /** Texto listo para mostrar en pantalla, ej.: "3 gastos · Total: Q 245.00" */
    suspend fun resumen(): String {
        val cantidad = repositorio.listar().size
        val palabra = if (cantidad == 1) "gasto" else "gastos"
        return "$cantidad $palabra · Total: ${formatearQuetzales(total())}"
    }

    // companion object = lo que en Java seria "static": pertenece a la clase, no a cada objeto.
    // Se crea UNA sola vez en lugar de crear el Regex en cada validacion.
    private companion object {
        val FORMATO_FECHA = Regex("""\d{4}-\d{2}-\d{2}""")
    }
}

/**
 * Funcion de nivel superior (fuera de la clase): se usa en consola y en Android.
 * Ejemplo: formatearQuetzales(45.5) -> "Q 45.50"
 */
fun formatearQuetzales(monto: Double): String = "Q %.2f".format(monto)
