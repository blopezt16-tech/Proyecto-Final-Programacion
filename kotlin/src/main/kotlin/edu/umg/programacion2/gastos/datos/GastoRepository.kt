package edu.umg.programacion2.gastos.datos

import edu.umg.programacion2.gastos.modelo.Gasto

/**
 * El "contrato" de donde se guardan los gastos. Es el mismo patron DAO que usamos
 * con MySQL en las clases de JDBC: quien lo usa NO sabe si los datos estan en una lista,
 * en MySQL o en la base de datos del telefono.
 *
 * Implementaciones previstas:
 *   1. GastoRepositoryEnMemoria  -> esta libreria (semanas 1 a 3)
 *   2. GastoRepositoryRoom       -> el proyecto Android (semana 4)
 *
 * IMPORTANTE: todas las funciones son "suspend".
 * suspend = "esta funcion puede tardar (ej. leer una base de datos); llamala desde una
 * corrutina para no congelar la pantalla". La lista en memoria no tarda nada, pero Room SI.
 * Si el contrato ya es suspend desde hoy, en la semana 4 NO hay que cambiarlo.
 *
 * Cuidado: una funcion suspend solo se puede llamar desde otra funcion suspend o desde
 * un bloque lifecycleScope.launch { ... } en Android.
 */
interface GastoRepository {

    /** Guarda el gasto y lo devuelve con su id asignado. */
    suspend fun agregar(gasto: Gasto): Gasto

    /** Devuelve una copia de todos los gastos guardados. */
    suspend fun listar(): List<Gasto>

    /** Devuelve el gasto o null si no existe (el ? obliga a quien llama a revisarlo). */
    suspend fun buscarPorId(id: Int): Gasto?

    /** Reemplaza el gasto con el mismo id. Devuelve false si ese id no existe. */
    suspend fun actualizar(gasto: Gasto): Boolean

    /** Elimina el gasto. Devuelve false si ese id no existe. */
    suspend fun eliminar(id: Int): Boolean
}
