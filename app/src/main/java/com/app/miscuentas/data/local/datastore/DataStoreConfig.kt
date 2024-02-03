package com.app.miscuentas.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.lang.Exception
import javax.inject.Inject


private const val DATASTORE_NAME = "datastore_mis_cuentas"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreConfig @Inject constructor(
    private val context: Context
) {
    //Metodo para guardar TRUE en caso de que se hayan registrado
    suspend fun putRegistroPreference(registradoOK: Boolean){
        val preferenceRegistro = booleanPreferencesKey(name = "RegistroOk")

        context.dataStore.edit { preferences ->
            preferences[preferenceRegistro] = registradoOK
        }
    }

    //Metodo que retorna el valor guardado en las preference
    suspend fun getRegistroPreference(): Boolean? {
        return try {
            val preferencesKey = booleanPreferencesKey(name = "RegistroOk")
            val preferences = context.dataStore.data.first()

            preferences[preferencesKey]

        }catch (e: Exception){
            null
        }
    }

    //Metodo para guardar TRUE en caso de que se hayan registrado
    suspend fun putInicoHuellaPreference(inicioHuella: Boolean){
        val preferenceHuella = booleanPreferencesKey(name = "InicoHuella")

        context.dataStore.edit { preferences ->
            preferences[preferenceHuella] = inicioHuella
        }
    }

    //Metodo que retorna el valor guardado en las preference
    suspend fun getInicoHuellaPreference(): Boolean? {
        return try {
            val preferenceHuella = booleanPreferencesKey(name = "InicoHuella")
            val preferences = context.dataStore.data.first()

            preferences[preferenceHuella]

        }catch (e: Exception){
            null
        }
    }

}