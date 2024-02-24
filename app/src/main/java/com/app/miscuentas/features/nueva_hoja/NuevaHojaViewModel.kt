package com.app.miscuentas.features.nueva_hoja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntityLin
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.model.HojaCalculo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class NuevaHojaViewModel @Inject constructor(
    private val repositoryParticipante: ParticipanteRepository,
    private val repositoryHojaCalculo: HojaCalculoRepository
) : ViewModel() {

    private val _eventoState = MutableStateFlow(NuevaHojaState())
    val eventoState: StateFlow<NuevaHojaState> = _eventoState

    fun onTituloFieldChanged(titulo: String) {
        _eventoState.value = _eventoState.value.copy(titulo = titulo)
    }

    fun onParticipanteFieldChanged(participante: String) {
        _eventoState.value = _eventoState.value.copy(participante = participante)
    }

    fun onLimiteGastoFieldChanged(limiteGasto: String) {
        _eventoState.value = _eventoState.value.copy(limiteGasto = limiteGasto)
    }

    fun onFechaCierreFieldChanged(fechaCierre: String) {
        _eventoState.value = _eventoState.value.copy(fechaCierre = fechaCierre)
    }

    /** METODOS PARA EL STATE DE PARTICIPANTES **/
    fun addParticipante(participante: Participante) {
        val updatedList = _eventoState.value.listaParticipantes + participante
        _eventoState.value = _eventoState.value.copy(
            listaParticipantes = updatedList,
            participante = ""
        )

    }

    fun deleteUltimoParticipante() {
        if (_eventoState.value.listaParticipantes.isNotEmpty()) {
            val updatedList = _eventoState.value.listaParticipantes.dropLast(1)
            _eventoState.value = _eventoState.value.copy(listaParticipantes = updatedList)
        }
    }

    fun getTotalParticipantes(): Int {
        return _eventoState.value.listaParticipantes.size
    }


    /** METODOS PARA LOS PARTICIPANTES EN ROOM **/
    //3_LLAMADA A INSERTAR PARTICIPANTES...
    fun insertAllParticipante(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){

                val insertPartOK = insertParticipante()
                if (insertPartOK){
                    inserAlltHojaCalculoLin()
                }
            }
        }
    }

    //4_INSERTAR PARTICIPANTES
    private suspend fun insertParticipante(): Boolean{
        return try{
            eventoState.value.listaParticipantes.forEach { participante ->
                val siguienteId = repositoryParticipante.getMaxIdParticipantes() + 1
                repositoryParticipante.insertAll(siguienteId, participante)
            }
            true
        }catch (e: Exception){
            false
        }
    }

    //Pinta una lista con los participantes almacenados en la BBDD
    fun getAllParticipantesToString(): String {//Borrar si no es necesario!!

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Se recolecta el Flow de forma asíncrona
                repositoryParticipante.getAllParticipantes().collect { participantes ->
                    _eventoState.value = _eventoState.value.copy(
                        listDbParticipantes = participantes.joinToString(
                            ", "
                        ) { it.nombre })
                }
            }
        }
        return _eventoState.value.listDbParticipantes
    }

    //Pinta una lista con los participantes del state
    fun getListaParticipatesStateString(): String {//Borrar si no es necesario!!
        var lista = ""
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                eventoState.value.listaParticipantes.forEach { participante ->
                    lista = participante.nombre + ','
                }
            }
        }
        return lista
    }

    /** METODOS PARA LA NUEVA HOJA EN ROOM **/
    fun instanceNuevaHoja(): HojaCalculo {
        val fecha: String? = _eventoState.value.fechaCierre.ifEmpty { null }

        return  HojaCalculo(
            0,
            _eventoState.value.titulo,
            fecha,
            _eventoState.value.limiteGasto.toDoubleOrNull(),
            _eventoState.value.status,
            _eventoState.value.listaParticipantes
        )
    }

    //1_LLAMADA A INSERTAR HOJA...
    fun insertAllHojaCalculo() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val insertOK = insertHojaCalculo()
                if (insertOK) {//si la insercion devuelve true, se insertan los participantes
                    insertAllParticipante()
                }
            }
        }
    }
    //2_INSERTAR HOJA
    private suspend fun insertHojaCalculo(): Boolean {
        val hoja = instanceNuevaHoja()

        return try {
            repositoryHojaCalculo.insertAllHojaCalculo(hoja)
            true // insert OK
        } catch (e: Exception) {
            false // insert NOK
        }
    }

    //5_LLAMADA A INSERTAR LINEAS DE HOJA....
    fun inserAlltHojaCalculoLin(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){

                val insertLinOK = insertHojaCalculoLin()
                if (insertLinOK) {//si la insercion devuelve true, se insertan los participantes
                    vaciarTextFields() //Vacio States
                }
            }
        }
    }

    //6_INSERTAR LINEAS HOJA
    private suspend fun insertHojaCalculoLin(): Boolean{
        var insertLinOK = false

        //Datos a para insertar:
        val id = repositoryHojaCalculo.getMaxIdHojasCalculos()
        var linea = 0

        eventoState.value.listaParticipantes.forEach { participante ->
            linea++
            val idParticipante = repositoryParticipante.getIdParticipante(participante.nombre)

            try {
                repositoryHojaCalculo.insertAllHojaCalculoLin(
                    DbHojaCalculoEntityLin(
                        id,
                        linea,
                        idParticipante,
                        "P"
                    )
                )
                insertLinOK = true
            } catch (e: Exception) {
                insertLinOK = false
            }
        }
        return insertLinOK
    }

    private fun vaciarTextFields(){
        _eventoState.value = _eventoState.value.copy(titulo = "")
        _eventoState.value = _eventoState.value.copy(listaParticipantes = emptyList())
        _eventoState.value = _eventoState.value.copy(limiteGasto = "")
        _eventoState.value = _eventoState.value.copy(fechaCierre = "")
    }

}