package com.app.miscuentas.features.inicio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.domain.SessionManager
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.domain.dto.UsuarioDto
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
    private val hojasService: HojasService,
    private val sessionManager: SessionManager
) : ViewModel()
{
    private val _inicioState = MutableStateFlow(InicioState())
    val inicioState: StateFlow<InicioState> = _inicioState


    fun onTotalHojasChanged(total: Int){
        _inicioState.value = _inicioState.value.copy(totalHojas = total)
    }
    fun onHojaPrincipalChanged(hojaPrincipal: HojaConParticipantes?){
        _inicioState.value = _inicioState.value.copy(hojaPrincipal = hojaPrincipal)
    }
    fun onIdHojaPrincipalChanged(idHojaPrincipal: Long){
        _inicioState.value = _inicioState.value.copy(idHojaPrincipal = idHojaPrincipal)
    }
    fun onRegistradoChanged(registrado: String){
        _inicioState.value = _inicioState.value.copy(registrado = registrado)
    }
    fun onHuellaDigitalChanged(withHuella: Boolean){
        _inicioState.value = _inicioState.value.copy(huellaDigital = withHuella)
    }

    fun onInicioHuellaChanged(permitido: Boolean){
         _inicioState.value = _inicioState.value.copy(huellaDigital = permitido)

        viewModelScope.launch {
            dataStoreConfig.putInicoHuellaPreference(permitido)
        }
    }

    fun cerrarSesion(permitido: Boolean, logeado: String){
        viewModelScope.launch {
            sessionManager.logout(permitido, logeado)
        }
    }

    //Compruebo si hay alguna hoja creada
    fun getAllHojasCalculos(){
        viewModelScope.launch {
            hojasService.getTotalHojasCreadas().collect{
                onTotalHojasChanged(it)
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
                        onHojaPrincipalChanged(null)
                        onIdHojaPrincipalChanged( 0)
                    } else {
                        onIdHojaPrincipalChanged(idHoja)
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
            hojasService.getHojaConParticipantes(idHoja).collect{ hoja ->
                withContext(Dispatchers.Main) {
                    onHojaPrincipalChanged(hoja)
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
            onRegistradoChanged(registrado)

            //Compruebo si debe quedar el check de la huella seleccionado en el Drawer
            val inicioHuella = dataStoreConfig.getInicoHuellaPreference()
            if (inicioHuella == "SI") onHuellaDigitalChanged(true)
        }
    }

}