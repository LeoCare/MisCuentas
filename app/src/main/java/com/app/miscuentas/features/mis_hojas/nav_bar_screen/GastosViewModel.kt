package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojaCalculoRepository: HojaCalculoRepository,
    private val repositoryParticipante: ParticipanteRepository,
    private val gastoRepository: GastoRepository
): ViewModel()
{

    private val _gastosState = MutableStateFlow(GastosState())
    val gastosState: StateFlow<GastosState> = _gastosState

    fun onBorrarGastoChanged(gasto: DbGastosEntity?){
        _gastosState.value = _gastosState.value.copy(gastoElegido = gasto)
    }

    fun onHojaAMostrar(idHoja: Long?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if (idHoja != null) {
                    hojaCalculoRepository.getHojaConParticipantes(idHoja).collect { hojaCalculo ->
                        _gastosState.value = _gastosState.value.copy(hojaAMostrar = hojaCalculo)
                        dataStoreConfig.putIdHojaPrincipalPreference(hojaCalculo!!.hoja.idHoja) //Actualizo DataStore con idhoja
                    }
                }
            }
        }
    }

    fun getHojaCalculoPrincipal(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hojaCalculoRepository.getHojaConParticipantes(_gastosState.value.idHojaPrincipal!!).collect {
                    _gastosState.value = _gastosState.value.copy(hojaAMostrar = it) //Actualizo state con idhoja
                    dataStoreConfig.putIdHojaPrincipalPreference(it?.hoja?.idHoja) //Actualizo DataStore con idhoja

                   // getListParticipantesToIdHoja(it.idHoja)
                }
            }
        }
    }

    /*
    suspend fun getListParticipantesToIdHoja(idHoja: Int) {
        viewModelScope.launch {
            repositoryParticipante.getListParticipantesToIdHoja(idHoja).collect { participantes ->
                coroutineScope {
                    val participantesConGastos = participantes.map { participante ->
                        async {
                            val gastos = getGastosParticipante(participante.id).first() // Recoge el primer valor del Flow
                            participante.copy(listaGastos = gastos)
                        }
                    }.awaitAll()

                    // Actualizo el estado aquí:
                    _gastosState.value = _gastosState.value.copy(
                        hojaAMostrar = _gastosState.value.hojaAMostrar?.copy(participantesHoja = participantesConGastos)
                    )
                }
            }
        }
    }



    fun getGastosParticipante(idParticipante: Int): Flow<List<Gasto?>> {
        val idHoja = _gastosState.value.hojaAMostrar!!.id
        return gastoRepository.getGastosParticipante(idHoja, idParticipante)
    }
    */
    init {
        viewModelScope.launch {
            val idUltimaHoja = dataStoreConfig.getIdHojaPrincipalPreference()
            _gastosState.value = _gastosState.value.copy(idHojaPrincipal = idUltimaHoja)
        }

    }

    /** ELIMINAR GASTO **/
    //Borrar
    suspend fun deleteGasto(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                gastoRepository.delete(gastosState.value.gastoElegido)
            }
        }
    }
    /************************/
}