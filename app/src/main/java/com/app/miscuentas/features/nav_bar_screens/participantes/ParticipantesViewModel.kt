package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.entitys.toDto
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.toDto
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.util.Validaciones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ParticipantesViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojasService: HojasService,
    private val participantesService: ParticipantesService
) : ViewModel(){

    private val _participantesState = MutableStateFlow(ParticipantesState())
    val participantesState: StateFlow<ParticipantesState> = _participantesState


    fun onParticipantesChanged(participantes: List<ParticipanteConGastos>){
        val listaParticipantes = _participantesState.value.listaParticipantes + participantes
        _participantesState.value = _participantesState.value.copy(listaParticipantes = listaParticipantes)
    }
    fun onHojaDelRegistradoChanged(hojas: List<HojaConParticipantes>){
        _participantesState.value = _participantesState.value.copy(hojasDelRegistrado = hojas)
    }
    fun onFiltroElegidoChanged(filtro: String){
        _participantesState.value = _participantesState.value.copy(filtroElegido = filtro)
        if (filtro == "Todos") onListaParticipantesAMostrarChanged(participantesState.value.listaParticipantes)
    }
    fun onListaParticipantesAMostrarChanged(participantesAMostrar: List<ParticipanteConGastos>){
        _participantesState.value = _participantesState.value.copy(listaParticipantesAMostrar = participantesAMostrar)
    }
    fun onOrdenElegidoChanged(orden: String){
        _participantesState.value = _participantesState.value.copy(ordenElegido = orden)
        ordenHoja()
    }
    fun onDescendingChanged(desc: Boolean){
        _participantesState.value = _participantesState.value.copy(descending = desc)
        ordenHoja()
    }
    fun onNavHojaElegidoChanged(hojaElegida: Long){
        _participantesState.value = _participantesState.value.copy(filtroHojaElegido = hojaElegida)
    }
    fun onFiltroHojaElegidoChanged(hojaElegida: Long){
        _participantesState.value = _participantesState.value.copy(filtroHojaElegido = hojaElegida)
        mostrarParticipantes()
    }
    fun onFiltroTipoElegidoChanged(tipoElegido: String){
        _participantesState.value = _participantesState.value.copy(filtroTipoElegido = tipoElegido)
        mostrarPorTipo()
    }
    fun onEleccionEnTituloChanged(eleccionEnTitulo: String){
        _participantesState.value = _participantesState.value.copy(eleccionEnTitulo = eleccionEnTitulo)
    }

    //Obtengo el id del registado
    fun getIdRegistroPreference() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            val idRegistrado = dataStoreConfig.getIdRegistroPreference()
            _participantesState.value = _participantesState.value.copy(idUsuario = idRegistrado)
            getAllHojaConParticipantes()
        }
    }

    /** Obtiene todas hojas con sus participantes desde Room **/
    fun getAllHojaConParticipantes() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val hojasDelRegistrado =  hojasService.getAllHojaConParticipantes()

            hojasDelRegistrado.collect { listaHojasConParticipantes ->
                onHojaDelRegistradoChanged(listaHojasConParticipantes)
                val todosParticipantes = listaHojasConParticipantes
                    .flatMap {
                            hojaConParticipantes ->
                        hojaConParticipantes.participantes
                    }
                onParticipantesChanged(todosParticipantes)
                mostrarParticipantes()
            }
        }
    }

    /** Metodo que actualiza el correo del participante **/
    fun onParticipanteWithCorreoChanged(participanteWithNewCorreo: DbParticipantesEntity) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            //Update desde API
            val participanteApi = participantesService.updateParticipanteAPI(participanteWithNewCorreo.toDto())
            if(participanteApi != null) {
                //Update en Room
                participantesService.update(participanteWithNewCorreo)
            }
        }
    }

    /** FILTRAR POR: **/
    private fun mostrarPorTipo() {
        val listaFiltrada = _participantesState.value.listaParticipantes
            .filter {
                it.participante.tipo == _participantesState.value.filtroTipoElegido
            }
        onListaParticipantesAMostrarChanged(listaFiltrada)
    }

    private fun mostrarParticipantes() {
        val hojaElegida: HojaConParticipantes
        val listaFiltrada: List<ParticipanteConGastos>

        if(participantesState.value.filtroHojaElegido > 0){
            onFiltroElegidoChanged("Hoja")
            hojaElegida = _participantesState.value.hojasDelRegistrado
                .first {
                    it.hoja.idHoja == _participantesState.value.filtroHojaElegido
                }
            onEleccionEnTituloChanged(hojaElegida.hoja.titulo)
            listaFiltrada =  hojaElegida.participantes
        }
         else listaFiltrada = _participantesState.value.hojasDelRegistrado.flatMap { it.participantes }

        onListaParticipantesAMostrarChanged(listaFiltrada)

    }
    /************************/


    /** ORDEN DE LAS HOJAS **/
    private fun ordenHoja() {
        val ordenado = participantesState.value.listaParticipantesAMostrar.let { lista ->
            val comparator = when (participantesState.value.ordenElegido) {
                "Tipo" -> compareBy<ParticipanteConGastos> { it.participante.tipo }
                    .thenBy { it.participante.tipo }
                "Nombre" -> compareBy<ParticipanteConGastos> { it.participante.nombre }
                    .thenBy { it.participante.tipo }
                else -> null
            }
            comparator?.let { if (participantesState.value.descending) lista.sortedWith(it.reversed()) else lista.sortedWith(it) }
        }
        if (ordenado != null) onListaParticipantesAMostrarChanged(ordenado)
    }
    /************************/
}