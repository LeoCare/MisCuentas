package com.app.miscuentas.features.nuevo_gasto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.miscuentas.data.domain.SessionManager
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.util.Validaciones
import com.app.miscuentas.data.model.Gasto
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.network.GastosService
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.dto.GastoCrearDto
import com.app.miscuentas.data.dto.GastoDto
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.util.Contabilidad.Contable.superaLimite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NuevoGastoViewModel @Inject constructor (
    private val hojasService: HojasService,
    private val gastosService: GastosService,
    private val sessionManager: SessionManager,
    private val dataStoreConfig: DataStoreConfig
): ViewModel() {


    private val _nuevoGastoState = MutableStateFlow(NuevoGastoState())
    val nuevoGastoState: StateFlow<NuevoGastoState> = _nuevoGastoState

    fun onIdRegistradoChanged(id: Long){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(idRegistrado = id)
    }
    fun onIdPagadorChanged(idPagador: Long){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(idPagador = idPagador)
    }
    fun onImporteTextFieldChanged(importe: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(importe = importe)
    }
    fun onIdGastoFieldChanged(idGasto: Long){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(idGastoElegido = idGasto)
    }
    fun onConceptoTextFieldChanged(concepto: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(concepto = concepto)
    }
    fun onPagadorChosen(pagador: ParticipanteConGastos){
        onIdPagadorChanged(pagador.participante.idParticipante)
        _nuevoGastoState.value = _nuevoGastoState.value.copy(participanteConGasto = pagador)
    }
    fun onSuperaLimiteChanged(supera: Boolean){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(superaLimite = supera)
    }
    fun onInsertOKChanged(insert: Boolean){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(insertOk = insert)
    }
    fun onInsertAPIOKChanged(insertAPI: Boolean){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(insertAPIOk = insertAPI)
    }
    fun onCierreSesionChanged(cerrar: Boolean){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(cierreSesion = cerrar)
    }

    //recojo valor de parametro pasado por en navController. Borrar si no es necesario!!
    fun onIdHojaPrincipalChanged(idHoja: Long?){
        viewModelScope.launch(Dispatchers.Main) {
            _nuevoGastoState.value = _nuevoGastoState.value.copy(idHoja = idHoja)
            getHojaCalculo()
        }
    }


    //Metodo que obtiene el idRegistro de la DataStore y actualiza dicho State
    suspend fun getIdRegistroPreference() {
        val idRegistro = dataStoreConfig.getIdRegistroPreference()
        if (idRegistro != null) {
            onIdRegistradoChanged(idRegistro)
            onIdPagadorChanged(idRegistro) //el primer checkbox marcado es el registrado
        }
    }


        //Actualizo la hojaActual
    suspend fun getHojaCalculo(){
        //Hoja a la cual sumarle este nuevo gasto
        val id = _nuevoGastoState.value.idHoja!!
        hojasService.getHojaConParticipantes(id).collect {
            _nuevoGastoState.value = _nuevoGastoState.value.copy(hojaActual = it) //Actualizo state con la hoja actual
        }
    }


    /** METODO QUE INSERTA EL GASTO **/
    fun insertaGasto(){
        val tipo = _nuevoGastoState.value.idGastoElegido
        val concepto = _nuevoGastoState.value.concepto
        val importe = _nuevoGastoState.value.importe.replace(',','.')
        val fechaGasto = Validaciones.fechaToStringFormat(LocalDate.now()) ?: LocalDate.now().toString()
        val idParticipante = _nuevoGastoState.value.idPagador
        val importeGasto = nuevoGastoState.value.importe
        val hoja = nuevoGastoState.value.hojaActual

        if(superaLimite(hoja, importeGasto)){
            onSuperaLimiteChanged(true)
        }
        else {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    //Insert desde API
                    val gastoAPIOk = insertGastoAPI(tipo, concepto, importe, fechaGasto, idParticipante, null)

                    val idGasto = gastoAPIOk?.idGasto ?:  0
                    //Insert en ROOM
                    val insertRoomOK = insertGastoRoom(idGasto, tipo, concepto, importe, fechaGasto, idParticipante)
                    if(insertRoomOK) {
                        vaciarTextFields()
                        onInsertOKChanged(true)
                    }
                }
            }
        }
    }

    suspend fun insertGastoAPI(
        tipo: Long,
        concepto: String,
        importe: String,
        fechaGasto: String,
        idParticipante: Long,
        idImagen: Long?
    ): GastoDto? {
        val gastoCrearDto = GastoCrearDto(tipo.toString(), concepto, importe, fechaGasto, idParticipante, idImagen)

        try{
            val gastoAPI = gastosService.createGastoAPI(gastoCrearDto)
            return gastoAPI
        }catch (e: Exception) {
            return null // inserción NOK
        }
    }

    fun insertGastoRoom(
        idGasto: Long?,
        tipo: Long,
        concepto: String,
        importe: String,
        fechaGasto: String,
        idParticipante: Long
    ): Boolean {
        val idGastoApi = idGasto ?: 0
        val gasto = Gasto( idGastoApi,tipo, concepto, importe, fechaGasto, null).toEntity(idParticipante)

        try{
            gastosService.insertaGasto(gasto)
            return true
        }catch (e: Exception) {

            return false // inserción NOK
        }
    }

    private fun vaciarTextFields(){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(importe = "")
        _nuevoGastoState.value = _nuevoGastoState.value.copy(concepto = "")
    }

    /** Cerrar sesion **/
    fun cerrarSesion(){
        viewModelScope.launch {
            sessionManager.logout()
        }
    }

}