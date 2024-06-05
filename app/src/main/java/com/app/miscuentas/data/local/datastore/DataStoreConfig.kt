package com.app.miscuentas.data.local.datastore

import android.content.Context
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

class DataStoreConfig @Inject constructor(
    private val context: Context
) {
    //Metodo que actualiza el usuario registrado
    suspend fun putRegistroPreference(registrado: String){
        val preferenceRegistro = stringPreferencesKey(name = "Registrado")
        val preferenceHuella = stringPreferencesKey(name = "InicioHuella")

        context.dataStore.edit { preferences ->

            if (registrado.isEmpty()){
                preferences.remove(preferenceRegistro)
                preferences.remove(preferenceHuella)
            }
            else {
                preferences.remove(preferenceHuella)
                preferences[preferenceRegistro] = registrado
            }
        }
    }

    //Metodo que retorna el nombre del usuario registrado
    suspend fun getRegistroPreference(): String? {
        return try {
            val preferencesKey = stringPreferencesKey(name = "Registrado")
            val preferences = context.dataStore.data.first()

            preferences[preferencesKey]

        }catch (e: Exception){
            null
        }
    }

    //Metodo para guardar el Id de la hoja principal
    suspend fun putIdRegistroPreference(idRegistro: Long?){
        val preferenceIdRegistro = longPreferencesKey(name = "IdRegistro")

        if (idRegistro != null) {
            context.dataStore.edit { preferences ->
                preferences[preferenceIdRegistro] = idRegistro
            }
        }
    }

    //Metodo que retorna el id del usuario registrado
    suspend fun getIdRegistroPreference(): Long? {
        return try {
            val preferencesKey = longPreferencesKey(name = "IdRegistro")
            val preferences = context.dataStore.data.first()

            preferences[preferencesKey]

        }catch (e: Exception){
            null
        }
    }

    //Metodo para guardar TRUE en caso de que se hayan registrado
    suspend fun putInicoHuellaPreference(inicioHuella: Boolean){
        val preferenceHuella = stringPreferencesKey(name = "InicioHuella")
        val inicioOk = if (inicioHuella) "SI" else "NO"
        context.dataStore.edit { preferences ->
            preferences[preferenceHuella] = inicioOk
        }
    }

    //Metodo que retorna el valor guardado en las preference
    suspend fun getInicoHuellaPreference(): String? {
        return try {
            val preferenceHuella = stringPreferencesKey(name = "InicioHuella")
            val preferences = context.dataStore.data.first()
            preferences[preferenceHuella]

        }catch (e: Exception){
            null
        }
    }

    //Metodo para guardar el Id de la hoja principal
    suspend fun putIdHojaPrincipalPreference(idHoja: Long?){
        val preferenceIdHoja = longPreferencesKey(name = "IdHojaPrincipal")

        if (idHoja != null) {
            context.dataStore.edit { preferences ->
                preferences[preferenceIdHoja] = idHoja
            }
        }
    }

    //Metodo que retorna el Id de la hoja principal
    suspend fun getIdHojaPrincipalPreference(): Long? {
        return try {
            val preferenceIdHoja = longPreferencesKey(name = "IdHojaPrincipal")
            val preferences = context.dataStore.data.first()

            preferences[preferenceIdHoja]

        }catch (e: Exception){
            null
        }
    }

}