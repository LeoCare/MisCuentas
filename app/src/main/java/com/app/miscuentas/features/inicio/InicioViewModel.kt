package com.app.miscuentas.features.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig  // DATASTORE
) : ViewModel()
{
    private val _inicioState = MutableStateFlow(InicoState())
    val inicioState: StateFlow<InicoState> = _inicioState


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

    init {
        viewModelScope.launch {
            val inicioHuella = dataStoreConfig.getInicoHuellaPreference()
            if (inicioHuella == true)  _inicioState.value = _inicioState.value.copy(huellaDigital = true)
        }
    }

}