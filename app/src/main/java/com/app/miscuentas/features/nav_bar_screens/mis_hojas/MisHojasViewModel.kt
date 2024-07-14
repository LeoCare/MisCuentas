package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.ParticipanteRepository
import com.app.miscuentas.util.Validaciones
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.toEntity
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
    private val repositoryHojaCalculo: HojaCalculoRepository,
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
        _misHojasState.value.hojaAModificar?.status = misHojasState.value.nuevoStatusHoja
        withContext(Dispatchers.IO) {
            repositoryHojaCalculo.updateHoja(misHojasState.value.hojaAModificar!!.toEntity())
            updatePreferenceIdHojaPrincipal(misHojasState.value.hojaAModificar!!.toEntity())
        }
    }

    //Actualizar status segun fecha cierre
    suspend fun updateStatusHojaForFechaCierre(hoja: DbHojaCalculoEntity) {
        hoja.status = "F"
        repositoryHojaCalculo.updateHoja(hoja)
        withContext(Dispatchers.IO){
            updatePreferenceIdHojaPrincipal(hoja)
        }
    }

    /** METODO QUE COMPRUEBA LA FECHA Y PROVOCA LA FINALIZACION SEGUN CORRESPONDA **/
    suspend fun compruebaFechaCierre(listaHojasConParticipantes: List<HojaConParticipantes>){
        listaHojasConParticipantes.forEach { hojaConParticipantes ->
            if(hojaConParticipantes.hoja.status == "C" && !hojaConParticipantes.hoja.fechaCierre.isNullOrEmpty()){
                val fechaCierreHoja = Validaciones.fechaToDateFormat(hojaConParticipantes.hoja.fechaCierre!!)
                val fechaActual = LocalDate.now()
                fechaCierreHoja?.let{
                    if (fechaCierreHoja < fechaActual){
                        updateStatusHojaForFechaCierre(hojaConParticipantes.hoja)
                    }
                }
            }
        }
    }

    //Eliminar
    fun deleteHojaConParticipantes() = viewModelScope.launch{
        withContext(Dispatchers.IO) {
            repositoryHojaCalculo.deleteHojaConParticipantes(misHojasState.value.hojaAModificar!!.toEntity())
            updatePreferenceIdHojaPrincipal(misHojasState.value.hojaAModificar!!.toEntity())
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
            repositoryHojaCalculo.getAllHojaConParticipantes(misHojasState.value.idRegistro).collect {listaHojasConParticipantes ->
                //Comprueba la fecha de cierre y la finaliza si corresponde
                compruebaFechaCierre(listaHojasConParticipantes)
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


