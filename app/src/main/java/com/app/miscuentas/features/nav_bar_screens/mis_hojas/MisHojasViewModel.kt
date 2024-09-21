package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.util.Validaciones
import com.app.miscuentas.data.model.HojaCalculo
import com.app.miscuentas.data.model.toDto
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.ParticipantesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MisHojasViewModel @Inject constructor(
    private val hojasService: HojasService,
    private val dataStoreConfig: DataStoreConfig,
    private val participantesService: ParticipantesService
): ViewModel(){

    private val _misHojasState by lazy { MutableStateFlow(MisHojasState()) }
    val misHojasState: StateFlow<MisHojasState> = _misHojasState


    fun onCircularIndicatorChanged(circular: Boolean){
        _misHojasState.value = _misHojasState.value.copy(circularIndicator = circular)
    }
    fun onHojaAModificarChanged(hoja: HojaCalculo){
        _misHojasState.value = _misHojasState.value.copy(hojaAModificar = hoja)
    }

    /** ORDEN DE LAS HOJAS **/
    //Metodo que define el orden elegido
    fun onTipoOrdenChanged(ordenElegido: String){
        _misHojasState.value = _misHojasState.value.copy(ordenElegido = ordenElegido)
        ordenHoja()
    }
    //Metodo que indica la direccion asc. o desc.
    fun onDescendingChanged(desc: Boolean){
        _misHojasState.value = _misHojasState.value.copy(descending = desc)
        ordenHoja()
    }

    private fun ordenHoja() {
        val ordenado = _misHojasState.value.listaHojasConParticipantes?.let { lista ->
            val comparator = when (_misHojasState.value.ordenElegido) {
                "Fecha Creacion" -> compareBy<HojaConParticipantes> { Validaciones.fechaToDateFormat(it.hoja.fechaCreacion) }
                "Fecha Cierre" -> compareBy { Validaciones.fechaToDateFormat(it.hoja.fechaCierre) }
                else -> null
            }
            comparator?.let { if (_misHojasState.value.descending) lista.sortedWith(it.reversed()) else lista.sortedWith(it) }
        }
        _misHojasState.value = _misHojasState.value.copy(listaHojasAMostrar = ordenado)
    }
    /************************/


    /** MOSTRAR POR: **/
    //Metodo que define el tipo a mostrar
    fun onMostrarFiltroChanged(filtroElegido: String){
        _misHojasState.value = _misHojasState.value.copy(filtroElegido = filtroElegido)
        mostrarSolo()
    }


    private fun mostrarSolo() {
        val filtrado = _misHojasState.value.listaHojasConParticipantes?.filter {
            when (_misHojasState.value.filtroElegido) {
                "A" -> it.hoja.status == "A"
                "F" -> it.hoja.status == "F"
                "C" -> it.hoja.status == "C"
                "B" -> it.hoja.status == "B"
                else -> true
            }
        }
        _misHojasState.value = _misHojasState.value.copy(listaHojasAMostrar = filtrado)
    }
    /************************/

    /** OPCIONES DE CADA HOJA **/
    fun onOpcionSelectedChanged(opcionElegida: String){
        _misHojasState.value = _misHojasState.value.copy(opcionSelected = opcionElegida)
    }

    //Cambio de status
    fun onStatusChanged(hojaCalculo: HojaCalculo, status: String){
        _misHojasState.value = _misHojasState.value.copy(hojaAModificar = hojaCalculo, nuevoStatusHoja = status)
    }

    //Actualizar status segun eleccion usuario
    fun updateStatusHoja() = viewModelScope.launch{
        val hojaCalculo = _misHojasState.value.hojaAModificar
        hojaCalculo?.let {
            it.status = misHojasState.value.nuevoStatusHoja //modifico estatus
            onHojaAModificarChanged(it) //guardo modificada

            withContext(Dispatchers.IO) {
                try {
                    //Update Room
                    hojasService.updateHoja(it.toEntity())
                    //Update DataStore
                    updatePreferenceIdHojaPrincipal(it.toEntity())
                    //Update Api
                    hojasService.updateHojaApi(it.toDto())

                } catch (e: Exception) {
                    onPendienteSubirCambiosChanged(true) //algo a fallado en las solicitudes
                }
            }
        }
    }


    //Eliminar
    fun deleteHojaConParticipantes() = viewModelScope.launch{
        val hojaCalculo = _misHojasState.value.hojaAModificar
        hojaCalculo?.let {
            withContext(Dispatchers.IO) {
                try{
                    //Delete Room
                    hojasService.deleteHojaConParticipantes(it.toEntity())
                    //Update DataStore
                    updatePreferenceIdHojaPrincipal(it.toEntity())
                    //Delete participantes desde Api
                    participantesService.getParticipantesBy("id_hoja", hojaCalculo.idHoja.toString())?.forEach {
                        participantesService.deleteParticipante(it.idParticipante)
                    }
                    //Delete hoja desde Api
                    hojasService.deleteHojaApi(it.idHoja)

                }catch (e: Exception) {
                    onPendienteSubirCambiosChanged(true) //algo a fallado en las solicitudes
                }

            }
        }
    }
    /************************/

    //Metodo que actualiza el preference del idHoja a 0 si no esta Activa.
    suspend fun updatePreferenceIdHojaPrincipal(hoja: DbHojaCalculoEntity) = viewModelScope.launch{
        withContext(Dispatchers.IO){
            if(hoja.status != "C") dataStoreConfig.putIdHojaPrincipalPreference(0)
            onOpcionSelectedChanged("")
        }
    }

    //Metodo que obtiene la lista de hojas, ordena por defecto y para el circularIndicator
    fun getAllHojaConParticipantes() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            hojasService.getAllHojaConParticipantes(misHojasState.value.idRegistro).collect {listaHojasConParticipantes ->
                //guardo la lista de hojas
                _misHojasState.value = _misHojasState.value.copy(listaHojasConParticipantes = listaHojasConParticipantes)

                mostrarSolo() //lleno la listaHojasAMostrar con la lista original
                delay(1000)
                onCircularIndicatorChanged(false)
            }
        }
    }



    //Metodo que obtiene el idRegistro de la DataStore y actualiza dicho State
    fun getIdRegistroPreference() = viewModelScope.launch {
        val idRegistro = dataStoreConfig.getIdRegistroPreference()
        if (idRegistro != null) {
            _misHojasState.value = _misHojasState.value.copy(idRegistro = idRegistro)

        }
    }

    /** Pendiente subir cambios a la red **/
    fun onPendienteSubirCambiosChanged(pendiente: Boolean){
        _misHojasState.value = _misHojasState.value.copy(pendienteSubirCambios = pendiente)
    }
}



