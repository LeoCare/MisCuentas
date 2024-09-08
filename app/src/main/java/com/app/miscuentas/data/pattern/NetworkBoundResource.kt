package com.app.miscuentas.data.pattern

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/** Esta clase define COMO OBTENER los datos (Room y Api) y COMO ACTUALIZARLOS.
 * El flujo es primero obtener los datos desde Room y si no, obtenerlo desde la Api..
 * ..y actualizar Room con ellos.
**/
abstract class NetworkBoundResource<ResultType, RequestType> {

    fun asFlow() = flow {
        emit(Resource.Loading<ResultType>())

        // Primero obtenemos los datos locales
        val localData = loadFromDb().firstOrNull()

        // Si los datos locales no son nulos, emitimos los datos
        if (shouldFetch(localData)) {
            // Si hay datos locales, emitimos esos datos primero
            localData?.let { emit(Resource.Success(it)) }

            try {
                // Hacemos la llamada a la red y guardamos los datos
                val apiResponse = fetchFromNetwork()
                if (apiResponse != null) {
                    saveNetworkResult(apiResponse)
                    // Después de guardar los datos de la red, cargamos de nuevo desde la base de datos
                    emitAll(loadFromDb().map { Resource.Success(it) })
                }
                else {
                    // Si la respuesta de la API es null, emitir error
                    emit(Resource.Success(null))
                }

            } catch (e: Exception) {
                // Si hay un error, emitimos los datos locales y el error
                emit(Resource.Error("Fallo en la red", localData))
            }
        } else {
            // Si no es necesario hacer fetch, emitimos los datos locales
            if (localData != null) {
                emit(Resource.Success(localData))
            } else {
                emit(Resource.Error<ResultType>("No hay datos locales disponibles", null))
            }
        }
    }

    // Definir las siguientes funciones para cada caso específico:
    protected abstract fun loadFromDb(): Flow<ResultType?> //carga los datos desde Room.
    protected abstract suspend fun fetchFromNetwork(): RequestType? //llamada a la Api.
    protected abstract suspend fun saveNetworkResult(item: RequestType) //guarda desde la Api a Room.
    protected open fun shouldFetch(data: ResultType?): Boolean = true //comprueba si es necesaria actualizacion desde la Api.
}

//Clase para manejar los estados de los datos
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
