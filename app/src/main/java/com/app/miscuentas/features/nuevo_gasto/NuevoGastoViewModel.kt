package com.app.miscuentas.features.nuevo_gasto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.util.Validaciones
import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.toEntity
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
    private val repositoryGasto: GastoRepository
): ViewModel()
{
    val _nuevoGastoState = MutableStateFlow(NuevoGastoState())
    val nuevoGastoState: StateFlow<NuevoGastoState> = _nuevoGastoState

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
        _nuevoGastoState.value = _nuevoGastoState.value.copy(idPagador = pagador.participante.idParticipante)
        _nuevoGastoState.value = _nuevoGastoState.value.copy(participanteConGasto = pagador)
    }

    fun onInsertOKChanged(insert: Boolean){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(insertOk = insert)
    }

    //recojo valor de parametro pasado por en navController. Borrar si no es necesario!!
    fun onIdHojaPrincipalChanged(idHoja: Long?){
        viewModelScope.launch(Dispatchers.Main) {
            _nuevoGastoState.value = _nuevoGastoState.value.copy(idHoja = idHoja)
            getHojaCalculo()
        }
    }

    //Actualizo la hojaActual
    suspend fun getHojaCalculo(){
        //Hoja a la cual sumarle este nuevo gasto
        val id = _nuevoGastoState.value.idHoja!!
        repositoryHojaCalculo.getHojaConParticipantes(id).collect {
            _nuevoGastoState.value = _nuevoGastoState.value.copy(hojaActual = it) //Actualizo state con la hoja actual
        }
    }

    fun insertaGasto(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val idParticipante = _nuevoGastoState.value.idPagador
                val gasto = instanciaNuevoGasto().toEntity(idParticipante)

                repositoryGasto.insertaGasto(gasto)
                vaciarTextFields()
                onInsertOKChanged(true)
            }
        }
    }

    fun instanciaNuevoGasto(): Gasto {
        val tipo = _nuevoGastoState.value.idGastoElegido
        val concepto = _nuevoGastoState.value.concepto
        val importe = _nuevoGastoState.value.importe
        val fechaGasto = Validaciones.fechaToStringFormat(LocalDate.now())

        return Gasto(
            idGasto = 0,
            tipo = tipo,
            concepto = concepto,
            importe = importe,
            fechaGasto = fechaGasto
        )
    }

    private fun vaciarTextFields(){
        _nuevoGastoState.value = _nuevoGastoState.value.copy(importe = "")
        _nuevoGastoState.value = _nuevoGastoState.value.copy(concepto = "")
    }

}