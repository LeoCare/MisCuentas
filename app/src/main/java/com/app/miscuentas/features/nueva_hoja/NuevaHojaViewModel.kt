package com.app.miscuentas.features.nueva_hoja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.Validaciones
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.domain.model.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
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


   /** LLAMADA A INSERTAR HOJA **/
    fun insertHoja() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val hoja = instanceNuevaHoja() //obtiene hojaEntity
                val participantesHoja = instaciaParticipantes(hoja.idHoja) //obtiene participantesEntitys

                insertrHojaConParticipantes(hoja, participantesHoja)
            }
        }
    }

    /** METODOS PARA LA NUEVA HOJA EN ROOM **/
    fun instanceNuevaHoja(): DbHojaCalculoEntity {
        val fechaCreacion: String? = Validaciones.fechaToStringFormat(LocalDate.now())
        val fechaCierre: String? = _nuevaHojaState.value.fechaCierre.ifEmpty { null }
        val idRegistro = _nuevaHojaState.value.idRegistro

        return  DbHojaCalculoEntity(
            titulo = _nuevaHojaState.value.titulo,
            fechaCreacion = fechaCreacion,
            fechaCierre = fechaCierre,
            limite = _nuevaHojaState.value.limiteGasto,
            status = _nuevaHojaState.value.status,
            idRegistroHoja = idRegistro
        )
    }

    /** INSTANCIA PARTICIPANTES CON IDHOJA **/
    fun instaciaParticipantes(hojaCalculoId: Long): List<DbParticipantesEntity> {
        val participantesHoja = mutableListOf<DbParticipantesEntity>()
        nuevaHojaState.value.listaParticipantes.forEach { participante ->
                //val siguienteId = repositoryParticipante.getMaxIdParticipantes() + 1
                participantesHoja.add(participante.toEntity(hojaCalculoId))
            }
        return participantesHoja
    }

    /** METODO PARA INSERTAR LA HOJA Y LOS PARTICIPANTES RELACIONADOS **/
    suspend fun insertrHojaConParticipantes(hoja: DbHojaCalculoEntity, participantes: List<DbParticipantesEntity>) {
        repositoryHojaCalculo.insertHojaConParticipantes(hoja, participantes)
        _nuevaHojaState.value = _nuevaHojaState.value.copy(insertOk = true)
    }


    //3_LLAMADA A INSERTAR PARTICIPANTES...
    fun insertAllParticipante(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){

//                //val insertPartOK = insertParticipante()
//                if (insertPartOK){
//                    _nuevaHojaState.value = _nuevaHojaState.value.copy(insertOk = true)
//                }
            }
        }
    }



    fun getHojaConParticipantes(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.getHojaConParticipantes(id).collect { hojaConParticipantes ->
                    _nuevaHojaState.value = _nuevaHojaState.value.copy(hojaConParticipantes = hojaConParticipantes)
                }
            }
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



/*
    //2_INSERTAR HOJA
    private suspend fun insertHojaCalculo(): Boolean {
        val hoja = instanceNuevaHoja()

        return try {
       //     repositoryHojaCalculo.insertAllHojaCalculo(hoja)
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


   //6_INTAR LINEAS HOJA (participantes)
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

    */

   fun putIdHojaPrincipalPreference(idHoja: Long){
       viewModelScope.launch {
           dataStoreConfig.putIdHojaPrincipalPreference(idHoja)
       }
   }

    fun getIdRegistroPreference(){
        viewModelScope.launch {
            val idRegistro = dataStoreConfig.getIdRegistroPreference()
            if (idRegistro != null) {
                _nuevaHojaState.value = _nuevaHojaState.value.copy(idRegistro = idRegistro)
            }
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
           //recojo el id de hoja maximo y el id del registrado
           getMaxIdHojasCalculos()
       }
   }
}