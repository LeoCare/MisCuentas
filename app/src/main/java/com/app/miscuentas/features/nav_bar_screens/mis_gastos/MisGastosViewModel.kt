package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.model.Gasto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MisGastosViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojaCalculoRepository: HojaCalculoRepository,
    private val gastoRepository: GastoRepository,
    private val participanteRepository: ParticipanteRepository
): ViewModel(){

    private val _misGastosState by lazy { MutableStateFlow(MisGastosState()) }
    val misGastosState: StateFlow<MisGastosState> = _misGastosState

    //Obtengo el id del registrado al inicio
    init {
       getIdRegistroPreference()
    }

    //Obtengo el id del registado
    fun getIdRegistroPreference() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            val idRegistrado = dataStoreConfig.getIdRegistroPreference()
            _misGastosState.value = _misGastosState.value.copy(idRegistro = idRegistrado)
            getParticipanteConGastos()
        }
    }

    fun getParticipanteConGastos() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            participanteRepository.getParticipanteConGastos(misGastosState.value.idRegistro!!).collect{
                _misGastosState.value = _misGastosState.value.copy(participanteConGastos = it)
            }

        }
    }


}