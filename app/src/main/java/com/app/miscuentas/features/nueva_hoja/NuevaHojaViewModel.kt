package com.app.miscuentas.features.nueva_hoja

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntityLin
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.Validaciones
import com.app.miscuentas.domain.model.HojaCalculo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class NuevaHojaViewModel @Inject constructor(
    private val repositoryParticipante: ParticipanteRepository,
    private val repositoryHojaCalculo: HojaCalculoRepository,
    private val dataStoreConfig: DataStoreConfig
) : ViewModel() {

    private val _nuevaHojaState = MutableStateFlow(NuevaHojaState())
    val nuevaHojaState: StateFlow<NuevaHojaState> = _nuevaHojaState

    fun onTituloFieldChanged(titulo: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(titulo = titulo)
    }

    fun onParticipanteFieldChanged(participante: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(participante = participante)
    }

    fun onLimiteGastoFieldChanged(limiteGasto: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(limiteGasto = limiteGasto)
    }

    fun onFechaCierreFieldChanged(fechaCierre: String) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(fechaCierre = fechaCierre)
    }

    fun onInsertOkFieldChanged(insert: Boolean) {
        _nuevaHojaState.value = _nuevaHojaState.value.copy(insertOk = insert)
        vaciarTextFields() //Vacio estados
    }

    /** METODOS PARA EL STATE DE PARTICIPANTES **/
    fun addParticipante(participante: Participante) {
        val updatedList = _nuevaHojaState.value.listaParticipantes + participante
        _nuevaHojaState.value = _nuevaHojaState.value.copy(
            listaParticipantes = updatedList,
            participante = ""
        )

    }

    fun deleteUltimoParticipante() {
        if (_nuevaHojaState.value.listaParticipantes.isNotEmpty()) {
            val updatedList = _nuevaHojaState.value.listaParticipantes.dropLast(1)
            _nuevaHojaState.value = _nuevaHojaState.value.copy(listaParticipantes = updatedList)
        }
    }

    fun getTotalParticipantes(): Int {
        return _nuevaHojaState.value.listaParticipantes.size
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
            nuevaHojaState.value.listaParticipantes.forEach { participante ->
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
                    _nuevaHojaState.value = _nuevaHojaState.value.copy(
                        listDbParticipantes = participantes.joinToString(
                            ", "
                        ) { it.nombre })
                }
            }
        }
        return _nuevaHojaState.value.listDbParticipantes
    }

    //Pinta una lista con los participantes del state
    fun getListaParticipatesStateString(): String {//Borrar si no es necesario!!
        var lista = ""
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                nuevaHojaState.value.listaParticipantes.forEach { participante ->
                    lista = participante.nombre + ','
                }
            }
        }
        return lista
    }

    /** METODOS PARA LA NUEVA HOJA EN ROOM **/
    fun instanceNuevaHoja(): HojaCalculo {
        val fechaCreacion: String? = Validaciones.fechaToStringFormat(LocalDate.now())
        val fechaCierre: String? = _nuevaHojaState.value.fechaCierre.ifEmpty { null }
        val hojaPrincipal = (_nuevaHojaState.value.maxIdHolaCalculo == 0)

        if (hojaPrincipal) putIdHojaPrincipalPreference(1) //por defecto, la primera en la principal

        return  HojaCalculo(
            id = 0, //no lo tiene en cuenta. En Room es autoincremental.
            titulo = _nuevaHojaState.value.titulo,
            fechaCreacion = fechaCreacion,
            fechaCierre = fechaCierre,
            limite = _nuevaHojaState.value.limiteGasto,
            status = _nuevaHojaState.value.status,
            participantesHoja = _nuevaHojaState.value.listaParticipantes,
            principal = hojaPrincipal
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

    //5_LLAMADA A INSERTAR LINEAS DE HOJA....(participantes)
    fun inserAlltHojaCalculoLin(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){

                val insertLinOK = insertHojaCalculoLin()
                if (insertLinOK) {//si la insercion devuelve true, se insertan los participantes
                    _nuevaHojaState.value = _nuevaHojaState.value.copy(insertOk = true)
                    getMaxIdHojasCalculos()
                }
            }
        }
    }

    //6_INSERTAR LINEAS HOJA (participantes)
    private suspend fun insertHojaCalculoLin(): Boolean{
        var insertLinOK = false

        //Datos para insertar:
        val id = _nuevaHojaState.value.maxIdHolaCalculo
        var linea = 0

        nuevaHojaState.value.listaParticipantes.forEach { participante ->
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

    fun putIdHojaPrincipalPreference(idHoja: Int){
        viewModelScope.launch {
            dataStoreConfig.putIdHojaPrincipalPreference(idHoja)
        }
    }

    suspend fun getMaxIdHojasCalculos(){
        repositoryHojaCalculo.getMaxIdHojasCalculos().collect { maxId ->
            _nuevaHojaState.value = _nuevaHojaState.value.copy(maxIdHolaCalculo = maxId)
        }
        putIdHojaPrincipalPreference(_nuevaHojaState.value.maxIdHolaCalculo)
    }


    private fun vaciarTextFields(){
        _nuevaHojaState.value = _nuevaHojaState.value.copy(titulo = "")
        _nuevaHojaState.value = _nuevaHojaState.value.copy(listaParticipantes = emptyList())
        _nuevaHojaState.value = _nuevaHojaState.value.copy(limiteGasto = "")
        _nuevaHojaState.value = _nuevaHojaState.value.copy(fechaCierre = "")
    }

    init {
        viewModelScope.launch {
            //recojo el id de hoja maximo
            getMaxIdHojasCalculos()

            //recojo la linea maxima de la ultima hoja
            val id = _nuevaHojaState.value.maxIdHolaCalculo
            repositoryHojaCalculo.getMaxLineaHojasCalculos(id).collect { maxLinea ->
                _nuevaHojaState.value = _nuevaHojaState.value.copy(maxLineaHolaCalculo = maxLinea)
            }
        }
    }
}