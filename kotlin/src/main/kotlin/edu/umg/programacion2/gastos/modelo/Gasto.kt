package edu.umg.programacion2.gastos.modelo

/**
 * Un gasto registrado por el usuario.
 *
 * data class -> Kotlin genera constructor, getters, toString(), equals(), hashCode() y copy()
 * (lo vimos en la Clase 11 con Estudiante).
 *
 * Decisiones:
 * - id = 0 significa "todavia no se ha guardado". El repositorio le asigna el id real,
 *   igual que hara Room (la base de datos del telefono) en la semana 4.
 * - fecha es un String "yyyy-MM-dd" (ej. "2026-10-03"): se ordena bien como texto y no
 *   necesita java.time, que en Android viejo (API < 26) no existe.
 * - monto es Double por simplicidad. Para dinero real se usa BigDecimal (ver Clase 11).
 */
data class Gasto(
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val categoria: Categoria,
    val fecha: String
)
