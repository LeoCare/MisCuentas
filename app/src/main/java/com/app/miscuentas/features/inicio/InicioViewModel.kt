package com.app.miscuentas.features.inicio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.util.Validaciones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
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
    fun getIdHojaPrincipalPreference(){
        viewModelScope.launch {
            try{
                val idHoja = withContext(Dispatchers.IO){ dataStoreConfig.getIdHojaPrincipalPreference()}
                if (idHoja != null){
                    if(idHoja.toInt() == 0){ //si es 0 no hay preferida
                        _inicioState.value = _inicioState.value.copy(hojaPrincipal = null)
                        _inicioState.value = _inicioState.value.copy(idHojaPrincipal = 0)
                    } else {
                        _inicioState.value = _inicioState.value.copy(idHojaPrincipal = idHoja)
                        getHojaPrincipal(idHoja)
                    }
                }
            }catch(e: Exception){
                Log.e("InicioViewModel", "Error en getIdHojaPrincipalPreference()", e)
            }

        }
    }

    //Obtengo la instancia de esa hoja
     private suspend fun getHojaPrincipal(idHoja: Long){
        withContext(Dispatchers.IO) {
            hojaCalculoRepository.getHojaConParticipantes(idHoja).collect{ hoja ->
                withContext(Dispatchers.Main) {
                    _inicioState.value = _inicioState.value.copy(hojaPrincipal = hoja)
                    compruebaFechaCierre(hoja)
                }
            }
        }
    }

    /** METODO QUE COMPRUEBA LA FECHA Y QUITA LA HOJA DEL ACCESO RAPIDO SI CORRESPONDE **/
    private suspend fun compruebaFechaCierre(hojaConParticipantes: HojaConParticipantes?){
        if(hojaConParticipantes?.hoja?.status == "C" && !hojaConParticipantes.hoja.fechaCierre.isNullOrEmpty()){
            val fechaCierreHoja = Validaciones.fechaToDateFormat(hojaConParticipantes.hoja.fechaCierre!!)
            val fechaActual = LocalDate.now()
            fechaCierreHoja?.let{
                if (fechaCierreHoja < fechaActual){
                    dataStoreConfig.putIdHojaPrincipalPreference(0)
                }
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