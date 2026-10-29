package edu.umg.programacion2.gastos.modelo

/**
 * enum class = un tipo con un conjunto FIJO de valores posibles.
 * Es el mismo concepto que enum en Java, pero cada valor puede recibir datos
 * en su constructor (aqui: el texto bonito que vera el usuario).
 *
 * IMPORTANTE: en Android este enum alimenta directamente el Spinner (lista desplegable)
 * de la pantalla "Registrar gasto". El Spinner muestra lo que devuelva toString().
 */
enum class Categoria(val nombreVisible: String) {
    ALIMENTACION("Alimentación"),
    TRANSPORTE("Transporte"),
    ESTUDIOS("Estudios"),
    ENTRETENIMIENTO("Entretenimiento"),
    SALUD("Salud"),
    OTROS("Otros");  // Cuidado: este punto y coma SI es obligatorio cuando el enum tiene cuerpo.

    // Sin este override el Spinner mostraria "ALIMENTACION" en lugar de "Alimentación".
    override fun toString(): String = nombreVisible
}
