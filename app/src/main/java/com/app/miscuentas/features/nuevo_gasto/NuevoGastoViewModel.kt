package com.app.miscuentas.features.nuevo_gasto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntityLinDet
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.model.Participante
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    fun onConceptoTextFieldChanged(concepto: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(concepto = concepto)
    }
    fun onPagadorChosen(pagador: String){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(pagador = pagador)

        //onIdPagadorChosen(pagador)

    }
    fun onPagadorRadioChanged(pagadorElegido: Boolean){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(pagadorElegido = pagadorElegido)
    }

    //recojo valor de parametro pasado por en navController. Borrar si no es necesario!!
    fun onIdHojaPrincipalChanged(idHoja: Int?){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(idHoja = idHoja)
        getHojaCalculo()
    }

//    fun onIdPagadorChosen(pagador: String){
//        viewModelScope.launch {
//            val idPagador = repositoryParticipante.getIdParticipante(pagador)
//            _nuevoGastoState.value = _nuevoGastoState.value.copy(idPagador = idPagador)
//        }
//    }

    //1_LLAMADA A INSERTAR LINEAS DETALLE EN HOJA....(gasto)
    fun insertAllHojaCalculoLinDet(){
        viewModelScope.launch{
            withContext(Dispatchers.IO){

                val insertLidDetOk = insertHojaCalculoLinDet()
                if (insertLidDetOk) {
                    _nuevoGastoState.value = _nuevoGastoState.value.copy(insertOk = true)
                    vaciarTextFields()
                }

            }
        }
    }

    //2_INSERTAR LINEAS DETALLE EN HOJA (gasto)
    private suspend fun insertHojaCalculoLinDet(): Boolean {
        val idHoja = _nuevoGastoState.value.idHoja!!
        val maxLinea = _nuevoGastoState.value.maxLineaHojaLin
        val lineaDet = _nuevoGastoState.value.maxLineaDetHolaCalculo + 1
        val idGasto = 0
        val concepto = _nuevoGastoState.value.concepto
        val importe = _nuevoGastoState.value.importe.toDouble()

        return try {
            repositoryHojaCalculo.insertAllHojaCalculoLinDet(
                DbHojaCalculoEntityLinDet(
                    id = idHoja,
                    linea = maxLinea,
                    linea_detalle = lineaDet,
                    id_gasto = idGasto,
                    concepto = concepto,
                    importe = importe
                )
            )
            true
        }catch (ex: Exception){
            false
        }
    }

    //Actualizo state hojaActual
    fun getHojaCalculo(){
        //Hoja a la cual sumarle este nuevo gasto
        val id = _nuevoGastoState.value.idHoja!!
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                repositoryHojaCalculo.getHojaCalculo(id).collect {
                    _nuevoGastoState.value = _nuevoGastoState.value.copy(hojaActual = it) //Actualizo state con la hoja actual
                    getListParticipantesToIdHoja(id) //seguido actualizo los pagadores de dicha hoja
                }
            }
        }

    }

    //Actualizo pagadore de la hojaActual
    suspend fun getListParticipantesToIdHoja(idHoja: Int?) {
        //Obtener participantes de la hoja y agregarlas al state
        if (idHoja != null) {
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
                    _nuevoGastoState.value =
                        _nuevoGastoState.value.copy(maxLineaHojaLin = Linea)
                }
            }
        }
    }

    private fun vaciarTextFields(){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(importe = "")
        _nuevoGastoState.value = _nuevoGastoState.value.copy(concepto = "")
    }

    //RECOGIDA DE DATOS DE LA DDBB
//    init {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//
//
//                //recojo la linea detalle maxima de la ultima hoja y linea
//                val idLinea = _nuevoGastoState.value.maxLineaHojaLin
//                repositoryHojaCalculo.getMaxLineaDetHojasCalculos(id, idLinea)
//                    .collect { maxLineaDet ->
//                        _nuevoGastoState.value =
//                            _nuevoGastoState.value.copy(maxLineaDetHolaCalculo = maxLineaDet)
//                    }
//
//
//            }
//        }
//    }
}