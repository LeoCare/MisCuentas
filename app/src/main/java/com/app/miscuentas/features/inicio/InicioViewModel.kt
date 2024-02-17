package com.app.miscuentas.features.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig, // DATASTORE
) : ViewModel()
{

    private val _inicioState = MutableStateFlow(InicioState())
    val inicioState: StateFlow<InicioState> = _inicioState


    fun onInicioHuellaChanged(permitido: Boolean){
         _inicioState.value = _inicioState.value.copy(huellaDigital = permitido)

        viewModelScope.launch {
            dataStoreConfig.putInicoHuellaPreference(permitido)
        }
    }

    fun onRegistroPreferenceChanged(login: Boolean){
        viewModelScope.launch {
            dataStoreConfig.putRegistroPreference(login)
        }
    }

    /** COMPROBACION DE INICIO CON HUELLA **/
    init {
        viewModelScope.launch {
            val inicioHuella = dataStoreConfig.getInicoHuellaPreference()
            if (inicioHuella == true)  _inicioState.value = _inicioState.value.copy(huellaDigital = true)
        }
    }

}