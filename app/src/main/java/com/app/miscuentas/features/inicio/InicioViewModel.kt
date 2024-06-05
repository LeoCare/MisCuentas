package com.app.miscuentas.features.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig, // DATASTORE
    private val hojaCalculoRepository: HojaCalculoRepository
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

    fun onRegistroPreferenceChanged(logeado: String){
        viewModelScope.launch {
            dataStoreConfig.putRegistroPreference(logeado)
        }
    }

    //Compruebo si hay alguna hoja creada
    fun getAllHojasCalculos(){
        viewModelScope.launch {
            hojaCalculoRepository.getTotalHojasCreadas().collect{
                _inicioState.value = _inicioState.value.copy(totalHojas = it)
            }
        }
    }

    //Compruebo si hay hoja para el acceso rapido
    suspend fun getIdHojaPrincipalPreference(){
        viewModelScope.launch {
            val idHoja = dataStoreConfig.getIdHojaPrincipalPreference()
            if (idHoja != null){
                if(idHoja.toInt() == 0){ //si es 0 no hay preferida
                    _inicioState.value = _inicioState.value.copy(hojaPrincipal = null)
                    _inicioState.value = _inicioState.value.copy(idHojaPrincipal = 0)
                } else {
                    _inicioState.value = _inicioState.value.copy(idHojaPrincipal = idHoja)
                    getHojaPrincipalPreference(idHoja)
                }
            }
        }
    }

    //Obtengo la instancia de esa hoja
     fun getHojaPrincipalPreference(idHoja: Long){
        viewModelScope.launch {
            hojaCalculoRepository.getHojaConParticipantes(idHoja).collect{ hoja ->
                _inicioState.value = _inicioState.value.copy(hojaPrincipal = hoja)
            }
        }
    }

    /** COMPROBACION PARA EL DRAWER (USUARIO Y CHECK DE HUELLA) **/
    init {
        viewModelScope.launch {
            //Guardo el nombre del usuario para mostrarlo en el Drawer
            val registrado = dataStoreConfig.getRegistroPreference().toString()
            _inicioState.value = _inicioState.value.copy(registrado = registrado)

            //Compruebo si debe quedar el check de la huella seleccionado en el Drawer
            val inicioHuella = dataStoreConfig.getInicoHuellaPreference()
            if (inicioHuella == "SI") _inicioState.value =
                _inicioState.value.copy(huellaDigital = true)



        }
    }

}