package com.app.miscuentas.features.nuevo_gasto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntityLinDet
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.Validaciones
import com.app.miscuentas.domain.model.Participante
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
    private val repositoryHojaCalculo: HojaCalculoRepository,
    private val repositoryParticipante: ParticipanteRepository
): ViewModel()
{
    val _nuevoGastoState = MutableStateFlow(NuevoGastoState())
    val nuevoGastoState: StateFlow<NuevoGastoState> = _nuevoGastoState
    fun onImporteTextFieldChanged(importe: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(importe = importe)
    }
    fun onIdGastoFieldChanged(idGasto: Int){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(idGastoElegdo = idGasto)
    }
    fun onConceptoTextFieldChanged(concepto: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(concepto = concepto)
    }
    fun onPagadorChosen(idPagador: Int){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(idPagador = idPagador)
        getLineaPartiHojasCalculosLin()

    }
    fun onPagadorRadioChanged(pagadorElegido: Boolean){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(pagadorElegido = pagadorElegido)
    }

    //recojo valor de parametro pasado por en navController. Borrar si no es necesario!!
    fun onIdHojaPrincipalChanged(idHoja: Int?){
        viewModelScope.launch(Dispatchers.Main) {
            _nuevoGastoState.value = _nuevoGastoState.value.copy(idHoja = idHoja)
            getHojaCalculo()
        }
    }


    //1_LLAMADA A INSERTAR LINEAS DETALLE EN HOJA....(gasto)
    fun insertAllHojaCalculoLinDet(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val insertLidDetOk = insertHojaCalculoLinDet()
                if (insertLidDetOk) {
                    _nuevoGastoState.value = _nuevoGastoState.value.copy(insertOk = true)
                    vaciarTextFields()
                }
                else _nuevoGastoState.value = _nuevoGastoState.value.copy(insertOk = false)
            }
        }

    }

    //2_INSERTAR LINEAS DETALLE EN HOJA (gasto)
    suspend fun insertHojaCalculoLinDet(): Boolean {
        val idHoja = _nuevoGastoState.value.idHoja!!
        val maxLinea = _nuevoGastoState.value.lineaHojaLin
        val lineaDet = _nuevoGastoState.value.maxLineaDetHolaCalculo!!.plus(1)
        val idGasto = _nuevoGastoState.value.idGastoElegdo
        val concepto = _nuevoGastoState.value.concepto.ifEmpty { "Varios" }
        val importe = _nuevoGastoState.value.importe

        return try {
            repositoryHojaCalculo.insertAllHojaCalculoLinDet(
                DbHojaCalculoEntityLinDet(
                    id = idHoja,
                    linea = maxLinea,
                    linea_detalle = lineaDet,
                    id_gasto = idGasto,
                    concepto = concepto,
                    importe = importe,
                    fecha_gasto = Validaciones.fechaToStringFormat(LocalDate.now())
                )
            )
            true
        }catch (ex: Exception){
            false
        }
    }

    //Actualizo state hojaActual
    suspend fun getHojaCalculo(){
        //Hoja a la cual sumarle este nuevo gasto
        val id = _nuevoGastoState.value.idHoja!!
        repositoryHojaCalculo.getHojaCalculo(id).collect {
            _nuevoGastoState.value = _nuevoGastoState.value.copy(hojaActual = it) //Actualizo state con la hoja actual
            getListParticipantesToIdHoja(id) //seguido actualizo los pagadores de dicha hoja
        }

    }


    //Actualizo pagadore de la hojaActual
    suspend fun getListParticipantesToIdHoja(idHoja: Int?) {
        //Obtener participantes de la hoja y agregarlas al state
        idHoja?.let {
            repositoryParticipante.getListParticipantesToIdHoja(idHoja).collect { participantes ->
                _nuevoGastoState.value.hojaActual?.participantesHoja = participantes
            }
        }
    }

    fun getLineaPartiHojasCalculosLin(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val idHoja = _nuevoGastoState.value.idHoja!!
                val pagador = _nuevoGastoState.value.idPagador
                repositoryHojaCalculo.getLineaPartiHojasCalculosLin(idHoja, pagador).collect { Linea ->
                    _nuevoGastoState.value = _nuevoGastoState.value.copy(lineaHojaLin = Linea)
                    getMaxLineaDetHojasCalculos()
                }
            }
        }
    }

    suspend fun getMaxLineaDetHojasCalculos() {
        //recojo la linea detalle maxima de la ultima hoja y linea
        val idHoja = _nuevoGastoState.value.idHoja!!
        val idLinea = _nuevoGastoState.value.lineaHojaLin
        repositoryHojaCalculo.getMaxLineaDetHojasCalculos(idHoja, idLinea)
            .collect { maxLineaDet ->
                if (maxLineaDet != null) {
                    _nuevoGastoState.value =
                        _nuevoGastoState.value.copy(maxLineaDetHolaCalculo = maxLineaDet)
                }
                else _nuevoGastoState.value =
                    _nuevoGastoState.value.copy(maxLineaDetHolaCalculo = 0)
            }
    }

    private fun vaciarTextFields(){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(importe = "")
        _nuevoGastoState.value = _nuevoGastoState.value.copy(concepto = "")
    }

}