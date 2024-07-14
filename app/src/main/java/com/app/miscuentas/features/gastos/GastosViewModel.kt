package com.app.miscuentas.features.gastos

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbFotoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.data.local.repository.BalanceRepository
import com.app.miscuentas.data.local.repository.FotoRepository
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.PagoRepository
import com.app.miscuentas.util.Contabilidad
import com.app.miscuentas.util.Contabilidad.Contable.totalGastosHoja
import com.app.miscuentas.util.Imagen.Companion.bitmapToByteArray
import com.app.miscuentas.util.Imagen.Companion.byteArrayToBitmap
import com.app.miscuentas.util.Validaciones
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojaCalculoRepository: HojaCalculoRepository,
    private val balanceRepository: BalanceRepository,
    private val gastoRepository: GastoRepository,
    private val fotoRepository: FotoRepository
): ViewModel() {

    private val _gastosState = MutableStateFlow(GastosState())
    val gastosState: StateFlow<GastosState> = _gastosState

    fun onBorrarGastoChanged(gasto: DbGastosEntity?){
        _gastosState.value = _gastosState.value.copy(gastoABorrar = gasto)
    }
    fun onNewFotoGastoChanged(gasto: DbGastosEntity?){
        _gastosState.value = _gastosState.value.copy(gastoNewFoto = gasto)
    }
    fun onSumaParticipantesChanged(sumaParticipantes: Map<String,Double>){
        _gastosState.value = _gastosState.value.copy(sumaParticipantes = sumaParticipantes)
    }
    fun onBalanceDeudaChanged(balanceDeuda: Map<String,Double>){
        _gastosState.value = _gastosState.value.copy(balanceDeuda = balanceDeuda)
    }
    fun onCierreAceptado(aceptado: Boolean) {
        _gastosState.value = _gastosState.value.copy(cierreAceptado = aceptado)
    }
    fun onImagenBitmapChanged(imagenBitmap: Bitmap?){
        _gastosState.value =_gastosState.value.copy(imagenBitmap = imagenBitmap)
    }
    fun onMostrarFotoChanged(mostrar: Boolean){
        _gastosState.value = _gastosState.value.copy(mostrarFoto = mostrar)
    }
    fun onTotalGastosActualChanged(total: Double){
        _gastosState.value = _gastosState.value.copy(totalGastosActual = total)
    }


    /** METODO QUE OBTIENE UNA HOJACONPARTICIPANTES DE LA BBDD:
     * Posteriormetne comprueba si existe un usuario que sea el registrado.
     * Ademas, actualiza el DataStore. **/
    fun onHojaAMostrar(idHoja: Long?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if (idHoja != null) {
                    hojaCalculoRepository.getHojaConParticipantes(idHoja).collect { hojaCalculo ->
                        _gastosState.value = _gastosState.value.copy(hojaAMostrar = hojaCalculo)
                        totalGastosHojaActual()
                        //Actualizo DataStore con idhoja si esta Activa
                        if (hojaCalculo?.hoja?.status == "C") {
                            dataStoreConfig.putIdHojaPrincipalPreference(hojaCalculo.hoja.idHoja)
                        }
                    }
                }
            }
        }
    }

    /** OBTIENE EL TOTAL DE GASTOS POR PARTICIPANTE PARA PINTAR EN 'RESUMEN' **/
    fun obtenerParticipantesYSumaGastos() {
        val hoja = gastosState.value.hojaAMostrar
        val mapaSumaParticipantes = Contabilidad.obtenerParticipantesYSumaGastos(hoja!!) as MutableMap<String, Double>
        onSumaParticipantesChanged(mapaSumaParticipantes)
    }

    fun totalGastosHojaActual(){
        val hoja = gastosState.value.hojaAMostrar
        val totalGastos = totalGastosHoja(hoja)
        onTotalGastosActualChanged(totalGastos)
    }

    /** METODO QUE CALCULA EL BALANCE:
     * Si la hoja esta finalizada el balance se obtiene de la BBDD
     * **/
    fun calcularBalance() {
        val hoja = gastosState.value.hojaAMostrar
        //si esta finalizada...tiene en cuenta los pagos realizados
        if (gastosState.value.hojaAMostrar?.hoja?.status == "F") {
            getHojaConBalanceFinal() //..obtengo una HojaConBalance y modifico el mapa(nombre, monto) desde t_balance
        }
        else {
            val balanceDeuda = Contabilidad.calcularBalance(hoja!!) as MutableMap<String, Double>
            onBalanceDeudaChanged(balanceDeuda)
        }
    }

    /** METODO QUE OBTIENE UNA HOJACONBALANCE DE LA BBDD Y CARGA AL INFO EN UNO DE LOS STATE **/
    /** Tiene en cuenta los pagos **/
    private fun getHojaConBalanceFinal() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hojaCalculoRepository.getHojaConBalances(gastosState.value.hojaAMostrar?.hoja!!.idHoja).collect{
                    _gastosState.value = _gastosState.value.copy(hojaConBalances = it)
                    calcularBalanceFinal() //con estos datos calculo el balance final
                }
            }
        }
    }


    /** METODO QUE PINTA UN MAPA CON EL PARTICIPANTE Y SU DEUDA **/
    /** Tiene en cuenta los pagos **/
    private fun calcularBalanceFinal(){
        val hoja = gastosState.value.hojaAMostrar
        val hojaConBalances = gastosState.value.hojaConBalances
        val balanceActual = Contabilidad.calcularBalance(hoja!!) as MutableMap<String, Double>
        val balanceDeuda = mutableMapOf<String, Double>()

        balanceActual.forEach { (nombre, monto) -> //para el primer nombre del mapa de balance
            hoja.participantes.forEach { participantes -> //..busco en la lista de participantes de la hoja
                if(participantes.participante.nombre == nombre) { //..el que coincida con el nombre
                    hojaConBalances?.balances?.forEach{balance -> //..busco en t_balance
                        if(participantes.participante.idParticipante == balance.idParticipanteBalance){ //..el que coincida con el idParticipante
                            balanceDeuda[nombre] = balance.monto
                        }
                    }
                }
            }
        }
        onBalanceDeudaChanged(balanceDeuda)
    }


    /** METODO QUE ELIMINA LA LINEA DE T_GASTOS **/
    //Borrar
    suspend fun deleteGasto(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                gastoRepository.delete(gastosState.value.gastoABorrar)
                totalGastosHojaActual()
            }
        }
    }

    /** METODO QUE ACTUALIZA LA FOTO DEL GASTO **/
    private suspend fun updateGastoWithFoto(idFoto: Long){
        gastosState.value.gastoNewFoto?.idFotoGasto = idFoto
        gastosState.value.gastoNewFoto?.let { gastoRepository.updateGasto(it) }
    }

    /** METODO QUE ACTUALIZA LA LINEA DE T_HOJA_CAB **/
    //Actualizar
    suspend fun updateHoja() = viewModelScope.launch{
        onCierreAceptado(false)
        _gastosState.value.hojaAMostrar?.hoja?.status = "F"
        withContext(Dispatchers.IO) {
            hojaCalculoRepository.updateHoja(gastosState.value.hojaAMostrar?.hoja!!)
            instanciarInsertarBalance()

        }
    }

    /** METODO QUE ACTUALIZA EL PREFERENCE DE IDHOJA A O PARA AQUELLA QUE NO ESTE ACTIVA **/
    private suspend fun updatePreferenceIdHojaPrincipal() {
        dataStoreConfig.putIdHojaPrincipalPreference(0)
    }


    /** REALIZAR BALANCE E INCERTAR EN T_BALANCE AL CERRAR LA HOJA **/
    private suspend fun instanciarInsertarBalance(){
        var balances : List<DbBalanceEntity> = listOf()
//        calcularBalance()
        gastosState.value.balanceDeuda?.forEach { (nombre, monto) ->
            val montoRedondeado = BigDecimal(monto).setScale(2, RoundingMode.HALF_UP).toDouble()
            val idParticipante =
                gastosState.value.hojaAMostrar?.participantes?.firstOrNull {
                    it.participante.nombre == nombre
                }?.participante?.idParticipante
            val deuda = DbBalanceEntity(
                idBalance = 0,
                idHojaBalance = gastosState.value.hojaAMostrar?.hoja?.idHoja!!,
                idParticipanteBalance = idParticipante!!,
                tipo = if(monto < 0) "D" else if(monto > 0) "A" else "B",
                monto = montoRedondeado //redondeo y con 2 decimales
            )
            balances = balances + deuda
        }

        insertBalancesForHoja(balances)
        updatePreferenceIdHojaPrincipal()
    }

    /** INSERTA DATOS EN T_BALANCE **/
    private suspend fun insertBalancesForHoja(balances: List<DbBalanceEntity>){
        balanceRepository.insertBalancesForHoja(gastosState.value.hojaAMostrar?.hoja!!, balances)
    }


    /** METODO QUE INSERTA LA FOTO Y ACTUALIZA EL GASTO **/
    fun insertImage(bitmap: Bitmap) {
        onImagenBitmapChanged(bitmap)
        val byteArray = bitmapToByteArray(bitmap, 50)
        val imageEntity = DbFotoEntity(imagen = byteArray)
        viewModelScope.launch {
            val idFoto = fotoRepository.insertFoto(imageEntity)
            updateGastoWithFoto(idFoto)
            onMostrarFotoChanged(true)
        }
    }

    /** METODO QUE OBTIENE LA FOTO DEL GASTO **/
    fun obtenerFotoGasto(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val imagen = fotoRepository.getFoto(id).imagen
                onImagenBitmapChanged(byteArrayToBitmap(imagen))
                onMostrarFotoChanged(true)
            }
        }
    }


}