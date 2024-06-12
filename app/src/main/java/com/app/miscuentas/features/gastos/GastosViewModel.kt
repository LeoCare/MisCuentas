package com.app.miscuentas.features.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.util.Contabilidad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    fun onResumenGastoChanged(mapaGastos: Map<String,Double>){
        _gastosState.value = _gastosState.value.copy(resumenGastos = mapaGastos)
    }
    fun onBalanceDeudaChanged(mapaDeuda: Map<String,Double>){
        _gastosState.value = _gastosState.value.copy(balanceDeuda = mapaDeuda)
    }

    fun onHojaAMostrar(idHoja: Long?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if (idHoja != null) {
                    hojaCalculoRepository.getHojaConParticipantes(idHoja).collect { hojaCalculo ->
                        _gastosState.value = _gastosState.value.copy(hojaAMostrar = hojaCalculo)
                        //Actualizo DataStore con idhoja si esta Activa
                        if (hojaCalculo?.hoja?.status == "C") {
                            dataStoreConfig.putIdHojaPrincipalPreference(hojaCalculo.hoja.idHoja)
                        }
                    }
                }
            }
        }
    }



    init {
        viewModelScope.launch {
            val idUltimaHoja = dataStoreConfig.getIdHojaPrincipalPreference()
            _gastosState.value = _gastosState.value.copy(idHojaPrincipal = idUltimaHoja)
        }

    }

    /** RESUMEN DE GASTOS POR PARTICIPANTES **/
    fun obtenerParticipantesYSumaGastos() {
        val hoja = gastosState.value.hojaAMostrar
        val mapaResumen = Contabilidad.obtenerParticipantesYSumaGastos(hoja!!) as MutableMap<String, Double>
        onResumenGastoChanged(mapaResumen)
    }
    /************************/

    /** BALANCE DE DEUDAS POR PARTICIPANTES **/
    fun calcularDeudas() {
        val hoja = gastosState.value.hojaAMostrar
        val mapaDeuda = Contabilidad.calcularDeudas(hoja!!) as MutableMap<String, Double>
        onBalanceDeudaChanged(mapaDeuda)
    }
    /************************/


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