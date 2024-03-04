package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.domain.GetMisHojas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HojasViewModel @Inject constructor(
    private val repositoryHojaCalculo: HojaCalculoRepository,
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

    init {
        viewModelScope.launch{
            repositoryHojaCalculo.getAllHojasCalculos().collect{ listHojasCalculo ->
                _hojasState.value = _hojasState.value.copy(listaHojas = listHojasCalculo)

                delay(1000)
                _hojasState.value = _hojasState.value.copy(circularIndicator = false)
            }
        }
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