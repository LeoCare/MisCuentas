package com.app.miscuentas.data.local.datastore

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.lang.Exception
import javax.inject.Inject


private const val DATASTORE_NAME = "datastore_mis_cuentas"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object DataStoreKeys {
    val VERSION = intPreferencesKey("VERSION")
    val REGISTRADO = stringPreferencesKey("REGISTRADO")
    val INICIOHUELLA = stringPreferencesKey("INICIOHUELLA")
    val IDREGISTRADO = longPreferencesKey("IDREGISTRADO")
    val IDHOJAPRINCIPAL = longPreferencesKey("IDHOJAPRINCIPAL")
    val PERMISOCAMARA = booleanPreferencesKey("PERMISOCAMARA")
    val PERMISOSALMACENAMIENTO = booleanPreferencesKey("PERMISOSALMACENAMIENTO")
    val ACCESS_TOKEN_KEY = stringPreferencesKey("ACCESS_TOKEN")
    val REFRESH_TOKEN_KEY = stringPreferencesKey("REFRESH_TOKEN")
}

class DataStoreConfig @Inject constructor(
    private val context: Context
) {

    /** Elimina todas las preferences **/
    suspend fun clearDataStore() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveDatabaseVersion(version: Int) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.VERSION] = version
        }
    }

    suspend fun getDatabaseVersion(): Int? {
        val preferences = context.dataStore.data.first()
        return preferences[DataStoreKeys.VERSION]
    }

    /** Metodo que actualiza el usuario registrado */
    suspend fun putRegistroPreference(registrado: String){
        context.dataStore.edit { preferences ->
            if (registrado.isEmpty()){
                preferences.remove(DataStoreKeys.REGISTRADO)
                preferences.remove(DataStoreKeys.INICIOHUELLA)
            }
            else {
                preferences.remove(DataStoreKeys.INICIOHUELLA)
                preferences[DataStoreKeys.REGISTRADO] = registrado
            }
        }
    }

    /** Metodo que retorna el nombre del usuario registrado */
    suspend fun getRegistroPreference(): String? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.REGISTRADO]

        }catch (e: Exception){
            null
        }
    }

    /** Metodo para guardar el Id de la hoja principal */
    suspend fun putIdRegistroPreference(idRegistro: Long?){
        if (idRegistro != null) {
            context.dataStore.edit { preferences ->
                preferences[DataStoreKeys.IDREGISTRADO] = idRegistro
            }
        }
    }

    /** Metodo que retorna el id del usuario registrado */
    suspend fun getIdRegistroPreference(): Long? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.IDREGISTRADO]

        }catch (e: Exception){
            null
        }
    }

    /** Metodo para guardar TRUE en caso de que se hayan registrado */
    suspend fun putInicoHuellaPreference(inicioHuella: Boolean){
        val inicioOk = if (inicioHuella) "SI" else "NO"
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.INICIOHUELLA] = inicioOk
        }
    }

    /** Metodo que retorna el valor guardado en las preference */
    suspend fun getInicoHuellaPreference(): String? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.INICIOHUELLA]

        }catch (e: Exception){
            null
        }
    }

    /** Metodo para guardar el Id de la hoja principal */
    suspend fun putIdHojaPrincipalPreference(idHoja: Long?){
        if (idHoja != null) {
            context.dataStore.edit { preferences ->
                preferences[DataStoreKeys.IDHOJAPRINCIPAL] = idHoja
            }
        }
    }

    /** Metodo que retorna el Id de la hoja principal */
    suspend fun getIdHojaPrincipalPreference(): Long? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.IDHOJAPRINCIPAL]

        }catch (e: Exception){
            null
        }
    }

    /** Metodo para guardar el estado del permiso de la camara */
    suspend fun putPermisoCamaraPreference(concedido: Boolean){
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.PERMISOCAMARA] = concedido
        }
    }

    /** Metodo que retorna el estado del permiso de la camara */
    suspend fun getPermisoCamaraPreference(): Boolean? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.PERMISOCAMARA]

        }catch (e: Exception){
            null
        }
    }

    /** Metodo para guardar el estado del permiso de la camara */
    suspend fun putPermisosPreference(concedido: Boolean){
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.PERMISOSALMACENAMIENTO] = concedido
        }
    }

    /** Metodo que retorna el estado del permiso de la camara */
    suspend fun getPermisosPreference(): Boolean? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.PERMISOSALMACENAMIENTO]

        }catch (e: Exception){
            null
        }
    }

    /** Guardar el token JWT en DataStore */
    suspend fun putAccessTokenPreference(token: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.ACCESS_TOKEN_KEY] = token
        }
    }

    /** Obtener el token JWT desde DataStore */
    suspend fun getAccessTokenPreference(): String? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.ACCESS_TOKEN_KEY]
        }catch (e: Exception){
            null
        }
    }

    /** Eliminar el token JWT de DataStore */
    suspend fun clearAccessTokenPreference() {
        context.dataStore.edit { preferences ->
            preferences.remove(DataStoreKeys.ACCESS_TOKEN_KEY)
        }
    }

    /** Guardar el token JWT en DataStore */
    suspend fun putRefreshTokenPreference(token: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.REFRESH_TOKEN_KEY] = token
        }
    }

    /** Obtener el token JWT desde DataStore */
    suspend fun getRefreshTokenPreference(): String? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.REFRESH_TOKEN_KEY]
        }catch (e: Exception){
            null
        }
    }

    /** Eliminar el token JWT de DataStore */
    suspend fun clearRefreshTokenPreference() {
        context.dataStore.edit { preferences ->
            preferences.remove(DataStoreKeys.REFRESH_TOKEN_KEY)
        }
    }
}