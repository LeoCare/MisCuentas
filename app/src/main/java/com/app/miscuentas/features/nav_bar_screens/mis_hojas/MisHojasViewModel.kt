package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.Validaciones
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MisHojasViewModel @Inject constructor(
    private val repositoryHojaCalculo: HojaCalculoRepository,
    private val repositoryParticipante: ParticipanteRepository,
    private val dataStoreConfig: DataStoreConfig
    /** API **/ // private val getMisHojas: GetMisHojas
): ViewModel(){

    private val _misHojasState by lazy { MutableStateFlow(MisHojasState()) }
    val misHojasState: StateFlow<MisHojasState> = _misHojasState


    fun onCircularIndicatorChanged(circular: Boolean){
        _misHojasState.value = _misHojasState.value.copy(circularIndicator = circular)
    }


    /** ORDEN DE LAS HOJAS **/
    //Metodo que define el orden elegido
    fun onTipoOrdenChanged(tipoOrden: String){
        _misHojasState.value = _misHojasState.value.copy(tipoOrden = tipoOrden)
        ordenHoja()
    }
    //Metodo que indica la direccion asc. o desc.
    fun onOrdenDescChanged(ordenDesc: Boolean){
        _misHojasState.value = _misHojasState.value.copy(ordenDesc = ordenDesc)
        ordenHoja()
    }

    //Metodo que ordena dependiendo de los estados elegidos en los dos metodos anteriores
    fun ordenHoja(){
        if(misHojasState.value.tipoOrden == "Fecha creacion"){
            if(misHojasState.value.ordenDesc){
                _misHojasState.value = _misHojasState.value.copy(
                    listaHojasAMostrar = misHojasState.value.listaHojasConParticipantes?.sortedByDescending {
                        Validaciones.fechaToDateFormat(it.hoja.fechaCreacion)
                    }
                )
            }
            else {
                _misHojasState.value = _misHojasState.value.copy(
                    listaHojasAMostrar = misHojasState.value.listaHojasConParticipantes?.sortedBy {
                        Validaciones.fechaToDateFormat(it.hoja.fechaCreacion)
                    }
                )
            }
        }
        else if (misHojasState.value.tipoOrden == "Fecha cierre"){
            if(misHojasState.value.ordenDesc){
                _misHojasState.value = _misHojasState.value.copy(
                    listaHojasAMostrar = misHojasState.value.listaHojasConParticipantes?.sortedByDescending {
                        Validaciones.fechaToDateFormat(it.hoja.fechaCierre)
                    }
                )
            }
            else {
                _misHojasState.value = _misHojasState.value.copy(
                    listaHojasAMostrar = misHojasState.value.listaHojasConParticipantes?.sortedBy {
                        Validaciones.fechaToDateFormat(it.hoja.fechaCierre)
                    }
                )
            }
        }

    }
    /************************/


    /** MOSTRAR POR: **/
    //Metodo que define el tipo a mostrar
    fun onMostrarTipoChanged(mostrarTipo: String){
        _misHojasState.value = _misHojasState.value.copy(mostrarTipo = mostrarTipo)
        mostrarSolo()
    }


    //Metodo que muestra solo las del tipo elegido en el metodo anterior
    fun mostrarSolo(){
        when(misHojasState.value.mostrarTipo){
            "A" -> _misHojasState.value = _misHojasState.value.copy(
                listaHojasAMostrar = misHojasState.value.listaHojasConParticipantes?.filter { it.hoja.status == "A" }
            )

            "F" -> _misHojasState.value = _misHojasState.value.copy(
                listaHojasAMostrar = misHojasState.value.listaHojasConParticipantes?.filter { it.hoja.status == "F" }
            )

            "C" -> _misHojasState.value = _misHojasState.value.copy(
                listaHojasAMostrar = misHojasState.value.listaHojasConParticipantes?.filter { it.hoja.status == "C" }
            )

            else -> _misHojasState.value = _misHojasState.value.copy(
                listaHojasAMostrar = misHojasState.value.listaHojasConParticipantes
            )

        }
    }
    /************************/



    /** OPCIONES DE CADA HOJA **/
    fun onOpcionSelectedChanged(opcionElegida: String){
        _misHojasState.value = _misHojasState.value.copy(opcionSelected = opcionElegida)
    }

    //Cambio de status
    fun onStatusChanged(hojaCalculo: HojaCalculo, status: String){
        _misHojasState.value = _misHojasState.value.copy(hojaAModificar = hojaCalculo)
        _misHojasState.value = _misHojasState.value.copy(nuevoStatusHoja = status)
    }

    //Actualizar
    suspend fun update(){
        _misHojasState.value.hojaAModificar?.status = misHojasState.value.nuevoStatusHoja
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.update(misHojasState.value.hojaAModificar!!)
                updatePreferenceIdHojaPrincipal()
            }
        }

    }

    //Eliminar
    suspend fun deleteHojaConParticipantes(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.deleteHojaConParticipantes(_misHojasState.value.hojaAModificar!!.toEntity())
                updatePreferenceIdHojaPrincipal()
            }
        }

    }
    /************************/

    //Metodo que actualiza el preference del idHoja a 0 si no esta Activa.
    fun updatePreferenceIdHojaPrincipal(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if(misHojasState.value.hojaAModificar!!.status != "C") dataStoreConfig.putIdHojaPrincipalPreference(0)
                onOpcionSelectedChanged("")
            }
        }

    }

    //Metodo que obtiene la lista de hojas, ordena por defecto y para el circularIndicator
    fun getAllHojaConParticipantes() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.getAllHojaConParticipantes(misHojasState.value.idRegistro).collect {listaHojasConParticipantes ->
                    //guardo la lista de hojas
                    _misHojasState.value = _misHojasState.value.copy(listaHojasConParticipantes = listaHojasConParticipantes)

                    mostrarSolo() //lleno la listaHojasAMostrar con la lista original
                    delay(1000)
                    _misHojasState.value = _misHojasState.value.copy(circularIndicator = false)
                }
            }
        }
    }

    //Metodo que obtiene el idRegistro de la DataStore y actualiza dicho State
    fun getIdRegistroPreference(){
        viewModelScope.launch {
            val idRegistro = dataStoreConfig.getIdRegistroPreference()
            if (idRegistro != null) {
                _misHojasState.value = _misHojasState.value.copy(idRegistro = idRegistro)
            }
        }
    }


    /** API **/
    //rellena la lista de hojas del state
//    suspend fun getPhotos(){
//        viewModelScope.launch {
//            try {
//                val hojas = getMisHojas.getPhotos()
//                if (hojas != null)  _hojasState.value = _hojasState.value.copy(listaHojas = hojas)
//                Log.d(ContentValues.TAG, "llamada a getMisHojas")
//            } catch (e: Exception) {
//                Log.d(ContentValues.TAG, "getMisHojas excepcion: $e")
//            }
//        }
//        delay(1000)
//        _hojasState.value = _hojasState.value.copy(circularIndicator = false)
//    }

    /** API **/
//    init {
//        viewModelScope.launch(){
//            getPhotos()
//        }
//    }
}