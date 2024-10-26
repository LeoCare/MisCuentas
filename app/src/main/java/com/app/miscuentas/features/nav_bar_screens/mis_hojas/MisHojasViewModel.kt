package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDto
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.model.HojaCalculo
import com.app.miscuentas.data.model.toDto
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.data.pattern.DataUpdates
import com.app.miscuentas.util.Validaciones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MisHojasViewModel @Inject constructor(
    private val hojasService: HojasService,
    private val dataStoreConfig: DataStoreConfig,
    private val participantesService: ParticipantesService,
    private val dataUpdates: DataUpdates
): ViewModel(){

    private val _misHojasState = MutableStateFlow(MisHojasState())
    val misHojasState: StateFlow<MisHojasState> = _misHojasState

    init {
        viewModelScope.launch {
            onIsRefreshingChanged(true)
            getIdRegistroPreference()
            getAllHojaConParticipantes()
            onIsRefreshingChanged(false)
        }
    }

    fun onIsRefreshingChanged(refrescar: Boolean){
        _misHojasState.update { currentState ->
            currentState.copy(isRefreshing = refrescar)
        }
    }
    fun onIdRegistradoChanged(idRegistrado: Long){
        _misHojasState.update { currentState ->
            currentState.copy(idRegistro = idRegistrado)
        }
    }
    fun onPendienteSubirCambiosChanged(pendiente: Boolean){
        _misHojasState.value = _misHojasState.value.copy(pendienteSubirCambios = pendiente)
    }
    fun onListaHojasConParticipantesChanged(hojasConParticipantes: List<HojaConParticipantes>){
        _misHojasState.update { currentState ->
            currentState.copy(listaHojasConParticipantes = hojasConParticipantes)
        }
    }
    fun onHojaAModificarChanged(hoja: HojaCalculo){
        _misHojasState.update { currentState ->
            currentState.copy(hojaAModificar = hoja)
        }
    }
    fun onHojasAMostrarChanged(hojas: List<HojaConParticipantes>){
        _misHojasState.update { currentState ->
            currentState.copy(listaHojasAMostrar = hojas)
        }
    }
    fun onFiltroElegidoChanged(filtro: String){
        _misHojasState.value = _misHojasState.value.copy(filtroElegido = filtro)
        if (filtro == "Todos") onHojasAMostrarChanged(misHojasState.value.listaHojasConParticipantes)
    }

    fun onFiltroTipoElegidoChanged(tipoElegido: String){
        _misHojasState.value = _misHojasState.value.copy(filtroTipoElegido = tipoElegido)
        mostrarPorTipo()
    }
    fun onFiltroEstadoElegidoChanged(estadoElegido: String){
        _misHojasState.value = _misHojasState.value.copy(filtroEstadoElegido = estadoElegido)
        mostrarPorEstado()
    }
    fun onEleccionEnTituloChanged(eleccionEnTitulo: String){
        _misHojasState.value = _misHojasState.value.copy(eleccionEnTitulo = eleccionEnTitulo)
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
        val ordenado = misHojasState.value.listaHojasConParticipantes.let { lista ->
            val comparator = when (_misHojasState.value.ordenElegido) {
                "Fecha Creacion" -> compareBy<HojaConParticipantes> { Validaciones.fechaToDateFormat(it.hoja.fechaCreacion) }
                "Fecha Cierre" -> compareBy { Validaciones.fechaToDateFormat(it.hoja.fechaCierre) }
                else -> null
            }
            comparator?.let { if (_misHojasState.value.descending) lista.sortedWith(it.reversed()) else lista.sortedWith(it) }
        }
        if (ordenado != null) {
            onHojasAMostrarChanged(ordenado)
        }
    }
    /************************/


    /** FILTRAR POR: **/
    private fun mostrarPorEstado() {
        val filtrado = misHojasState.value.listaHojasConParticipantes.filter {
            when (misHojasState.value.filtroEstadoElegido) {
                "A" -> it.hoja.status == "A"
                "F" -> it.hoja.status == "F"
                "C" -> it.hoja.status == "C"
                "B" -> it.hoja.status == "B"
                else -> true
            }
        }
        onHojasAMostrarChanged(filtrado)
    }

    private fun mostrarPorTipo() {
        // Filtrar las hojas según la lógica deseada
        val listaFiltrada = _misHojasState.value.listaHojasConParticipantes.filter { hojaConParticipantes ->
            when (misHojasState.value.filtroTipoElegido) {
                "Propietaria" -> hojaConParticipantes.hoja.propietaria == "S"
                "Invitado" -> hojaConParticipantes.hoja.propietaria == "N" && hojaConParticipantes.participantes.any { it.participante.idUsuarioParti == misHojasState.value.idRegistro }
                "SinConfirmar" -> hojaConParticipantes.hoja.propietaria == "N" && hojaConParticipantes.participantes.all { it.participante.idUsuarioParti != misHojasState.value.idRegistro }
                else -> true
            }
        }
        onHojasAMostrarChanged(listaFiltrada)
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

    /** Actualizar status segun eleccion usuario **/
    fun updateStatusHoja() = viewModelScope.launch{
        val hojaCalculo = misHojasState.value.hojaAModificar
        hojaCalculo?.let {
            it.status = misHojasState.value.nuevoStatusHoja //modifico estatus
            onHojaAModificarChanged(it) //guardo modificada

            withContext(Dispatchers.IO) {
                try {
                    onIsRefreshingChanged(true)
                    //Update Room
                    hojasService.updateHoja(it.toEntity())
                    //Update DataStore
                    updatePreferenceIdHojaPrincipal(it.toEntity())
                    //Update Api
                    hojasService.updateHojaApi(it.toDto())
                    getAllHojaConParticipantes()
                    onIsRefreshingChanged(false)
                } catch (e: Exception) {
                    onPendienteSubirCambiosChanged(true) //algo a fallado en las solicitudes
                }
            }
        }
    }

    //Colocar idUsuario = TRUE o eliminar correo = FALSE
    fun updateUnirmeHoja(acepto: Boolean) = viewModelScope.launch{
        val hojaCalculo = misHojasState.value.hojaAModificar
        val hojaConParticipantes = misHojasState.value.listaHojasAMostrar.find { it.hoja.idHoja == hojaCalculo?.idHoja }
        val miCorreo = dataStoreConfig.getCorreoRegistroPreference()

        hojaConParticipantes?.let {
            val participanteAModificar = it.participantes.find { hoja -> hoja.participante.correo == miCorreo }

            participanteAModificar?.participante?.let { participante ->
                try {
                    if(acepto) {//Unirme
                        participante.idUsuarioParti = misHojasState.value.idRegistro
                        participante.tipo = "ONLINE"
                        onIsRefreshingChanged(true)
                        //Update Room
                        participantesService.update(participante)
                        //Update Api
                        participantesService.updateParticipanteAPI(participante.toDto())
                        getAllHojaConParticipantes()
                        onIsRefreshingChanged(false)
                    }
                    else { //No unirme
                        participante.correo = null
                        onIsRefreshingChanged(true)
                        //Update Room
                        hojasService.deleteHojaConParticipantes(hojaConParticipantes.hoja)
                        //Update Api
                        participantesService.updateParticipanteAPI(participante.toDto())
                        getAllHojaConParticipantes()
                        onIsRefreshingChanged(false)
                    }
                }
                catch (e: Exception) {
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
                    participantesService.getParticipantesByAPI("id_hoja", hojaCalculo.idHoja.toString())?.forEach {
                        participantesService.deleteParticipanteAPI(it.idParticipante)
                    }
                    //Delete hoja desde Api
                    hojasService.deleteHojaApi(it.idHoja)

                }catch (e: Exception) {
                    onPendienteSubirCambiosChanged(true) //algo a fallado en las solicitudes
                }
            }
        }
        getAllHojaConParticipantes()
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
    suspend fun getAllHojaConParticipantes() {
        withContext(Dispatchers.IO) {
            val listaHojasConParticipantes = hojasService.getHojasConParticipantes()
            //guardo la lista de hojas
            onListaHojasConParticipantesChanged(listaHojasConParticipantes)
            mostrarPorEstado()
        }
    }


    //Metodo que obtiene el idRegistro de la DataStore y actualiza dicho State
    suspend fun getIdRegistroPreference()  {
        val idRegistro = dataStoreConfig.getIdRegistroPreference()
        if (idRegistro != null) {
            onIdRegistradoChanged(idRegistro)
        }
    }

    fun ActualizarDatos(){
        viewModelScope.launch {
            onIsRefreshingChanged(true)
            dataUpdates.limpiarYVolcarLogin(misHojasState.value.idRegistro)
            getAllHojaConParticipantes()
            onIsRefreshingChanged(false)
        }
    }



}



