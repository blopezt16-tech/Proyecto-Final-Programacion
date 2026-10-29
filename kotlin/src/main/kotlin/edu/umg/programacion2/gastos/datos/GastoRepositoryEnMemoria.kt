package edu.umg.programacion2.gastos.datos

import edu.umg.programacion2.gastos.modelo.Gasto

/**
 * Primera implementacion del contrato: guarda los gastos en una lista.
 * Cuidado: al cerrar la app (o el programa de consola) los datos se pierden.
 * Eso se resuelve en la semana 4 con Room, sin tocar las pantallas.
 */
class GastoRepositoryEnMemoria : GastoRepository {

    // 1. Encapsulamiento: la lista es private; nadie de afuera puede modificarla directo.
    private val gastos = mutableListOf<Gasto>()

    // 2. Contador para generar ids, como el AUTO_INCREMENT de MySQL.
    private var siguienteId = 1

    override suspend fun agregar(gasto: Gasto): Gasto {
        // 3. copy() crea un Gasto nuevo igual al recibido pero con el id asignado.
        //    El Gasto original no cambia porque sus propiedades son val.
        val guardado = gasto.copy(id = siguienteId)
        siguienteId++
        gastos.add(guardado)
        return guardado
    }

    // IMPORTANTE: toList() devuelve una COPIA. Si devolvieramos "gastos" directamente,
    // quien llama podria convertirla a MutableList y romper el encapsulamiento.
    override suspend fun listar(): List<Gasto> = gastos.toList()

    override suspend fun buscarPorId(id: Int): Gasto? {
        for (gasto in gastos) {
            if (gasto.id == id) {
                return gasto
            }
        }
        return null
    }

    override suspend fun actualizar(gasto: Gasto): Boolean {
        val posicion = buscarPosicion(gasto.id)
        if (posicion == -1) {
            return false
        }
        gastos[posicion] = gasto  // en Kotlin lista[i] = x equivale a lista.set(i, x) de Java
        return true
    }

    override suspend fun eliminar(id: Int): Boolean {
        val posicion = buscarPosicion(id)
        if (posicion == -1) {
            return false
        }
        gastos.removeAt(posicion)
        return true
    }

    // 4. Funcion auxiliar privada: devuelve la posicion en la lista o -1 si no esta.
    private fun buscarPosicion(id: Int): Int {
        for (posicion in gastos.indices) {  // indices = 0..(size - 1)
            if (gastos[posicion].id == id) {
                return posicion
            }
        }
        return -1
    }
}
