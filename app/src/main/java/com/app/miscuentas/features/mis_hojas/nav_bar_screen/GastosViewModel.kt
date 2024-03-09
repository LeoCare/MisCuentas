package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.domain.model.HojaCalculo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val repositoryHojaCalculo: HojaCalculoRepository
): ViewModel()
{

    private val _gastosState = MutableStateFlow(GastosState())
    val gastosState: StateFlow<GastosState> = _gastosState


    fun onHojaAMostrar(idHoja: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if (idHoja != null) {
                    repositoryHojaCalculo.getHojaCalculo(idHoja).collect {
                        _gastosState.value = _gastosState.value.copy(hojaAMostrar = it)
                    }
                }
            }
        }
    }

    fun getHojaCalculoPrincipal(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.getHojaCalculoPrincipal().collect {
                    _gastosState.value = _gastosState.value.copy(hojaAMostrar = it) //Actualizo state con idhoja
                    dataStoreConfig.putIdHojaPrincipalPreference(it?.id) //Actualizo DataStore con idhoja
                }
            }
        }
    }
//    init {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                repositoryHojaCalculo.getHojaCalculoPrincipal().collect {
//                    _gastosState.value = _gastosState.value.copy(hojaPrincipal = it) //Actualizo state con idhoja
//                    dataStoreConfig.putIdHojaPrincipalPreference(it?.id) //Actualizo DataStore con idhoja
//                }
//            }
//        }
//    }

}