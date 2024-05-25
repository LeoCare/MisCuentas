package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.GetMisHojas
import com.app.miscuentas.domain.Validaciones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HojasViewModel @Inject constructor(
    private val repositoryHojaCalculo: HojaCalculoRepository,
    private val repositoryParticipante: ParticipanteRepository,
    private val dataStoreConfig: DataStoreConfig
    /** API **/ // private val getMisHojas: GetMisHojas
): ViewModel(){

    private val _hojasState by lazy { MutableStateFlow(HojasState()) }
    val hojasState: StateFlow<HojasState> = _hojasState


    //Guarda la hojaPrincipal en el dataStore al presionar el CheckBox
    fun onHojaPrincipalChanged(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.getHojaCalculoPrincipal().collect {
                    _hojasState.value = _hojasState.value.copy(hojaPrincipal = it) //Actualizo state con idhoja

                    val idHoja = _hojasState.value.hojaPrincipal?.id
                    dataStoreConfig.putIdHojaPrincipalPreference(idHoja) //Actualizo DataStore con idhoja
                }
            }
        }
    }


    /** METODO QUE SE EJECUTA EN UNA CORRUTINA LLAMANDO A UN METODO QUE RECOLECTA DATOS
     * QUE A SU VEZ LLAMA A UNA FUNCION SUSPEND DE MANERA ASINCRONA PARA CADA DATO RECOLECTADO.
     * ESTO HACE QUE SE EJECUTEN LAS SUSPEND TODAS A LA VEZ EN HILOS SEPARADOS.
     */
    fun getAllHojasCalculos() {
        viewModelScope.launch {
            repositoryHojaCalculo.getAllHojasCalculos().collect { listHojasCalculo ->
                //guardo la liste hojas
                _hojasState.value = _hojasState.value.copy(listaHojas = listHojasCalculo)

                //obtego la lista de participantes de cada una de ellas
                listHojasCalculo.forEachIndexed { index, hoja ->
                    launch {
                        getListParticipantesToIdHoja(index, hoja.id)
                    }
                }
                delay(3000)
                _hojasState.value = _hojasState.value.copy(circularIndicator = false)
            }
        }
    }

    //Actualizo participantes de las hojas
    suspend fun getListParticipantesToIdHoja(index: Int, idHoja: Int) {
        //Obtener participantes de la hoja y agregarlas al state
        repositoryParticipante.getListParticipantesToIdHoja(idHoja).collect { participantes ->
            _hojasState.value.listaHojas?.get(index)?.participantesHoja = participantes
        }
    }

    fun ordenHoja(){
        _hojasState.value = _hojasState.value.copy(
            listaHojas = hojasState.value.listaHojas?.sortedBy { Validaciones.fechaToDateFormat(it.fechaCreacion) }
        )
    }
    fun ordenHojadesc(){
        _hojasState.value = _hojasState.value.copy(
            listaHojas = hojasState.value.listaHojas?.sortedByDescending { Validaciones.fechaToDateFormat(it.fechaCreacion) }
        )
    }

    /** API **/
    //rellena la lista de hojas del state
//    suspend fun getPhotos(){
//        viewModelScope.launch {
//            try {
//                val hojas = getMisHojas.getPhotos()
//                if (hojas != null)  _hojasState.value = _hojasState.value.copy(listaHojas = hojas)
//                Log.d(ContentValues.TAG, "llamada a getMisHojas")
//            } catch (e: Exception) {
//                Log.d(ContentValues.TAG, "getMisHojas excepcion: $e")
//            }
//        }
//        delay(1000)
//        _hojasState.value = _hojasState.value.copy(circularIndicator = false)
//    }

    /** API **/
//    init {
//        viewModelScope.launch(){
//            getPhotos()
//        }
//    }
}