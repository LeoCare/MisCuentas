package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.util.Validaciones
import com.app.miscuentas.util.Contabilidad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MisGastosViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojaCalculoRepository: HojaCalculoRepository
): ViewModel(){

    private val _misGastosState by lazy { MutableStateFlow(MisGastosState()) }
    val misGastosState: StateFlow<MisGastosState> = _misGastosState

    //Obtengo el id del registrado al inicio
    init {
       getIdRegistroPreference()
    }
    fun onGastosDelParticipanteChanged(gastos: List<DbGastosEntity>){
        val listaGastos = _misGastosState.value.listaGastos + gastos
        _misGastosState.value = _misGastosState.value.copy(listaGastos = listaGastos)
    }
    fun onHojaDelRegistradoChanged(hojas: List<HojaConParticipantes>){
        _misGastosState.value = _misGastosState.value.copy(hojasDelRegistrado = hojas)
    }
    fun onFiltroElegidoChanged(filtro: String){
        _misGastosState.value = _misGastosState.value.copy(filtroElegido = filtro)
        if (filtro == "Todos") onListaGastosAMostrarChanged(misGastosState.value.listaGastos)
    }
    fun onOrdenElegidoChanged(orden: String){
        _misGastosState.value = _misGastosState.value.copy(ordenElegido = orden)
        ordenHoja()
    }
    fun onDescendingChanged(desc: Boolean){
        _misGastosState.value = _misGastosState.value.copy(descending = desc)
        ordenHoja()
    }
    fun onFiltroHojaElegidoChanged(hojaElegida: Long){
        _misGastosState.value = _misGastosState.value.copy(filtroHojaElegido = hojaElegida)
        mostrarHoja()
    }
    fun onFiltroTipoElegidoChanged(tipoElegido: Long){
        _misGastosState.value = _misGastosState.value.copy(filtroTipoElegido = tipoElegido)
        mostrarTipo()
    }
    fun onListaGastosAMostrarChanged(listaGastosAMostrar: List<DbGastosEntity>?){
        _misGastosState.value = _misGastosState.value.copy(listaGastosAMostrar = listaGastosAMostrar)
        totalGastos()
    }
    fun onEleccionEnTituloChanged(eleccionEnTitulo: String){
        _misGastosState.value = _misGastosState.value.copy(eleccionEnTitulo = eleccionEnTitulo)
    }
    fun onSumaGastosChanged(suma: Double){
        _misGastosState.value = _misGastosState.value.copy(sumaGastos = suma)
    }

    //Obtengo el id del registado
    fun getIdRegistroPreference() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            val idRegistrado = dataStoreConfig.getIdRegistroPreference()
            _misGastosState.value = _misGastosState.value.copy(idRegistro = idRegistrado)
            getAllHojaConParticipantes()
        }
    }


    fun getAllHojaConParticipantes() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val idRegistro = misGastosState.value.idRegistro ?: return@withContext
            val hojasDelRegistrado =  hojaCalculoRepository.getAllHojaConParticipantes(idRegistro)

            hojasDelRegistrado.collect { listaHojasConParticipantes ->
                onHojaDelRegistradoChanged(listaHojasConParticipantes)
                val gastosDelRegistrado = listaHojasConParticipantes
                    .flatMap {
                        hojaConParticipantes ->
                        hojaConParticipantes.participantes
                    }
                    .filter { it.participante.idRegistroParti == idRegistro }
                    .flatMap { it.gastos }

                onGastosDelParticipanteChanged(gastosDelRegistrado)
                onListaGastosAMostrarChanged(gastosDelRegistrado)
            }
        }
    }

    /** FILTRAR POR: **/
    private fun mostrarTipo() {
        val listaFiltrada = _misGastosState.value.listaGastos
            .filter {
                it.tipo == _misGastosState.value.filtroTipoElegido
            }
        onListaGastosAMostrarChanged(listaFiltrada)
    }

    private fun mostrarHoja() {
        val idRegistro = misGastosState.value.idRegistro
        val listaFiltrada = _misGastosState.value.hojasDelRegistrado
            .filter {
                it.hoja.idHoja == _misGastosState.value.filtroHojaElegido
            }
            .flatMap { it.participantes }
            .filter { it.participante.idRegistroParti == idRegistro }
            .flatMap{ it.gastos }

        onListaGastosAMostrarChanged(listaFiltrada)
    }
    /************************/


    /** ORDEN DE LAS HOJAS **/
    private fun ordenHoja() {
        val ordenado = misGastosState.value.listaGastosAMostrar?.let { lista ->
            val comparator = when (misGastosState.value.ordenElegido) {
                "Tipo" -> compareBy<DbGastosEntity> { it.concepto }
                "Importe" -> compareBy { it.importe.replace(",",".").toDouble()}
                "Fecha" ->  compareBy { Validaciones.fechaToDateFormat(it.fechaGasto) }
                else -> null
            }
            comparator?.let { if (misGastosState.value.descending) lista.sortedWith(it.reversed()) else lista.sortedWith(it) }
        }
        if (ordenado != null) onListaGastosAMostrarChanged(ordenado)

    }
    /************************/

    private fun totalGastos() {
        val lista = misGastosState.value.listaGastosAMostrar
        val suma = Contabilidad.totalGastos(lista)
        onSumaGastosChanged(suma)
    }
}