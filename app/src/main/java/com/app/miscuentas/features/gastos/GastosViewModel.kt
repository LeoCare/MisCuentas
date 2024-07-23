package com.app.miscuentas.features.gastos

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbFotoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.repository.BalanceRepository
import com.app.miscuentas.data.local.repository.FotoRepository
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.util.Contabilidad
import com.app.miscuentas.util.Contabilidad.Contable.calcularBalanceFinal
import com.app.miscuentas.util.Contabilidad.Contable.instanciarBalance
import com.app.miscuentas.util.Imagen.Companion.bitmapToByteArray
import com.app.miscuentas.util.Imagen.Companion.byteArrayToBitmap
import com.app.miscuentas.util.Validaciones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    fun onHojaAMostrarChanged(hoja: HojaConParticipantes?){
        _gastosState.value = _gastosState.value.copy(hojaAMostrar = hoja)
    }


    /** METODO QUE OBTIENE UNA HOJACONPARTICIPANTES DE LA BBDD:
     * Posteriormetne comprueba si existe un usuario que sea el registrado.
     * Ademas, actualiza el DataStore. **/
    fun onHojaAMostrar(idHoja: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO){
                    hojaCalculoRepository.getHojaConParticipantes(idHoja).collect { hojaCalculo ->
                        withContext(Dispatchers.Main) {
                            onHojaAMostrarChanged(hojaCalculo)
                            totalGastosHojaActual()
                        }
                        //Compruebo si se paso la fecha de cierre
                        compruebaFechaCierre(hojaCalculo)
                    }
                }
            }catch(e: Exception){
                Log.e("GastosViewModel", "Error en onHojaAMostrar()", e)
            }
        }
    }

    /** METODO QUE COMPRUEBA LA FECHA Y PROVOCA LA FINALIZACION SEGUN CORRESPONDA **/
    suspend fun compruebaFechaCierre(hojaConParticipantes: HojaConParticipantes?){
        if(hojaConParticipantes?.hoja?.status == "C" && !hojaConParticipantes.hoja.fechaCierre.isNullOrEmpty()){
            val fechaCierreHoja = Validaciones.fechaToDateFormat(hojaConParticipantes.hoja.fechaCierre!!)
            val fechaActual = LocalDate.now()
            fechaCierreHoja?.let{
                if (fechaCierreHoja < fechaActual){
                    onCierreAceptado(true)
                }
                else {//Actualizo DataStore con idhoja si esta Activa
                    dataStoreConfig.putIdHojaPrincipalPreference(hojaConParticipantes.hoja.idHoja)
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
        val totalGastos = Contabilidad.totalGastosHoja(hoja)
        onTotalGastosActualChanged(totalGastos)
    }

    /** METODO QUE CALCULA EL BALANCE:
     * Si la hoja esta finalizada el balance se obtiene de la BBDD
     * **/
    fun calcularBalance() {
        val hoja = gastosState.value.hojaAMostrar
        //si esta finalizada o balanceada...tiene en cuenta los pagos realizados
        if (gastosState.value.hojaAMostrar?.hoja?.status != "C") {
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
                    calculoFinal() //con estos datos calculo el balance final
                }
            }
        }
    }


    /** METODO QUE CALCULA EL BALANCE FINAL AL FINALIZAR LA HOJA **/
    /** Tiene en cuenta los pagos **/
    private fun calculoFinal(){
        val hoja = gastosState.value.hojaAMostrar
        val hojaConBalances = gastosState.value.hojaConBalances
        val balanceDeuda = calcularBalanceFinal(hoja, hojaConBalances)

        onBalanceDeudaChanged(balanceDeuda)
    }


    /** METODO QUE ELIMINA LA LINEA DE T_GASTOS **/
    //Borrar
    suspend fun deleteGasto(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                gastosState.value.gastoABorrar?.let { gastoRepository.delete(it) }
                withContext(Dispatchers.Main) {
                    totalGastosHojaActual()
                }
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
    suspend fun updateHoja(status: String) = viewModelScope.launch{
        onCierreAceptado(false)
        _gastosState.value.hojaAMostrar?.hoja?.status = status
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
        val balanceDeuda = gastosState.value.balanceDeuda
        val hojaAMostrar = gastosState.value.hojaAMostrar
        val balances = instanciarBalance(balanceDeuda, hojaAMostrar)

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