package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.domain.Validaciones
import com.app.miscuentas.domain.model.HojaCalculo
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
//     fun mostrarSolo(){
//        when(hojasState.value.mostrarTipo){
//            "A" -> _hojasState.value = _hojasState.value.copy(
//                listaHojasAMostrar = hojasState.value.listaHojas?.filter { it.status == "A" }
//            )
//
//            "F" -> _hojasState.value = _hojasState.value.copy(
//                listaHojasAMostrar = hojasState.value.listaHojas?.filter { it.status == "F" }
//            )
//
//            "C" -> _hojasState.value = _hojasState.value.copy(
//                listaHojasAMostrar = hojasState.value.listaHojas?.filter { it.status == "C" }
//            )
//
//            else -> _hojasState.value = _hojasState.value.copy(
//                listaHojasAMostrar = hojasState.value.listaHojas
//            )
//
//        }
//    }

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

    //Borrar
    suspend fun update(){
        _misHojasState.value.hojaAModificar?.status = misHojasState.value.nuevoStatusHoja
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.update(misHojasState.value.hojaAModificar!!)
            }
        }
    }
    /************************/

    //Guarda la hojaPrincipal en el dataStore al presionar el CheckBox
    fun onHojaPrincipalChanged(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.getHojaCalculoPrincipal().collect {
                    _misHojasState.value = _misHojasState.value.copy(hojaPrincipal = it) //Actualizo state con idhoja

                    val idHoja = _misHojasState.value.hojaPrincipal?.idHoja
                    dataStoreConfig.putIdHojaPrincipalPreference(idHoja) //Actualizo DataStore con idhoja
                }
            }
        }
    }


    /** METODO QUE SE EJECUTA EN UNA CORRUTINA LLAMANDO A UN METODO QUE RECOLECTA DATOS
     * QUE A SU VEZ LLAMA A UNA FUNCION SUSPEND DE MANERA ASINCRONA PARA CADA DATO RECOLECTADO.
     * ESTO HACE QUE SE EJECUTEN LAS SUSPEND TODAS A LA VEZ EN HILOS SEPARADOS.
     */
//    fun getAllHojasCalculos() {
//        viewModelScope.launch {
//            repositoryHojaCalculo.getAllHojasCalculos().collect { listHojasCalculo ->
//                //guardo la liste hojas
//                _hojasState.value = _hojasState.value.copy(listaHojas = listHojasCalculo)
//
//                //obtego la lista de participantes de cada una de ellas
//                listHojasCalculo.forEachIndexed { index, hoja ->
//                    launch {
//                        //getListParticipantesToIdHoja(index, hoja.idHoja)
//                    }
//                }
//                mostrarSolo()
//                delay(3000)
//                _hojasState.value = _hojasState.value.copy(circularIndicator = false)
//            }
//        }
//
//    }

    fun getAllHojaConParticipantes() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repositoryHojaCalculo.getAllHojaConParticipantes().collect { listaHojasConParticipantes ->
                    //guardo la lista de hojas
                    _misHojasState.value = _misHojasState.value.copy(listaHojasConParticipantes = listaHojasConParticipantes)

                    mostrarSolo() //lleno la listaHojasAMostrar con la lista original
                    delay(1000)
                    _misHojasState.value = _misHojasState.value.copy(circularIndicator = false)
                }
            }
        }

    }

    /*
    //Actualizo participantes de las hojas
    suspend fun getListParticipantesToIdHoja(index: Int, idHoja: Int) {
        //Obtener participantes de la hoja y agregarlas al state
        repositoryParticipante.getListParticipantesToIdHoja(idHoja).collect { participantes ->
            _hojasState.value.listaHojas?.get(index)?.participantesHoja = participantes
        }

    }

     */

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