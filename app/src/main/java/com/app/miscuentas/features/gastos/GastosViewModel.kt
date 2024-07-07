package com.app.miscuentas.features.gastos

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
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
import com.app.miscuentas.util.Validaciones
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val pagoRepository: PagoRepository,
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
    fun onBalanceDeudaChanged(mapaDeuda: Map<String,Double>){
        _gastosState.value = _gastosState.value.copy(balanceDeuda = mapaDeuda)
    }
    fun onCierreAceptado(aceptado: Boolean) {
        _gastosState.value = _gastosState.value.copy(cierreAceptado = aceptado)
    }
    fun onPagoRealizadoChanged(realizado: Boolean){
        _gastosState.value = _gastosState.value.copy(pagoRealizado = realizado)
    }
    fun onImagenPagoChanged(imagen: String?){
        _gastosState.value = _gastosState.value.copy(imagenPago = imagen)
    }
    fun onListaPagosConParticipantesChanged(listaPagosConParticipantes: List<PagoConParticipantes>){
        _gastosState.value = _gastosState.value.copy(listaPagosConParticipantes = listaPagosConParticipantes)
    }
    fun onImageUriChanged(imageUri: Uri?){
        _gastosState.value = _gastosState.value.copy(imageUri = imageUri)
    }
    fun onImageAbsolutePathChanged(imageAbsolutePath: String?){
        _gastosState.value = _gastosState.value.copy(imageAbsolutePath = imageAbsolutePath)
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
                        _gastosState.value = _gastosState.value.copy(idRegistrado = dataStoreConfig.getIdRegistroPreference()!!)
                        comprobarExisteRegistrado()
                        //Actualizo DataStore con idhoja si esta Activa
                        if (hojaCalculo?.hoja?.status == "C") {
                            dataStoreConfig.putIdHojaPrincipalPreference(hojaCalculo.hoja.idHoja)
                        }
                    }
                }
            }
        }
    }

    /** METODO QUE COMPRUEBA SI EN LA HOJA EXISTE UN PARTICIPANTE PARA EL REGISTRADO **/
    fun comprobarExisteRegistrado(){
        val hoja = gastosState.value.hojaAMostrar
        hoja?.participantes?.forEach {
            if(it.participante.idRegistroParti == gastosState.value.idRegistrado ){
                _gastosState.value = _gastosState.value.copy(existeRegistrado = true)
                return
            }
        }
    }

    /** OBTIENE EL TOTAL DE GASTOS POR PARTICIPANTE PARA PINTAR EN 'RESUMEN' **/
    fun obtenerParticipantesYSumaGastos() {
        val hoja = gastosState.value.hojaAMostrar
        val mapaSumaParticipantes = Contabilidad.obtenerParticipantesYSumaGastos(hoja!!) as MutableMap<String, Double>
        onSumaParticipantesChanged(mapaSumaParticipantes)
    }


    /** METODO QUE CALCULA EL BALANCE:
     * Si la hoja esta finalizada el balance se obtiene de la BBDD
     * **/
    fun calcularBalance() {
        val hoja = gastosState.value.hojaAMostrar
        val mapaDeuda = Contabilidad.calcularBalance(hoja!!) as MutableMap<String, Double>
        onBalanceDeudaChanged(mapaDeuda)
        //si esta finalizada...
        if (gastosState.value.hojaAMostrar?.hoja?.status == "F") {
            getHojaConBalances() //..obtengo una HojaConBalance y modifico el mapa(nombre, monto) desde t_balance
        }
    }

    /** METODO QUE CALCULA EL BALANCE AL FINALIZAR LA HOJA **/
    fun calcularBalanceFinal(){
        val hoja = gastosState.value.hojaAMostrar
        val hojaConBalances = gastosState.value.hojaConBalances
        val balanceActual = gastosState.value.balanceDeuda
        val balances = mutableMapOf<String, Double>()

        balanceActual?.forEach { (nombre, monto) -> //para el primer nombre del mapa de balance
            hoja?.participantes?.forEach { participantes -> //..busco en la lista de participantes de la hoja
                if(participantes.participante.nombre == nombre) { //..el que coincida con el nombre
                    hojaConBalances?.balances?.forEach{balance -> //..busco en t_balance
                        if(participantes.participante.idParticipante == balance.idParticipanteBalance){ //..el que coincida con el idParticipante
                            balances[nombre] = balance.monto
                        }
                    }
                }
            }
        }
        onBalanceDeudaChanged(balances)
    }


    /** METODO QUE ELIMINA LA LINEA DE T_GASTOS **/
    //Borrar
    suspend fun deleteGasto(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                gastoRepository.delete(gastosState.value.gastoABorrar)
            }
        }
    }

    /** METODO QUE ACTUALIZA LA FOTO DEL GASTO **/
    suspend fun updateFoto(idFoto: Long){
        gastosState.value.gastoNewFoto!!.idFotoGasto = idFoto
        gastoRepository.updateFoto(gastosState.value.gastoNewFoto!!)
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
    suspend fun updatePreferenceIdHojaPrincipal() {
        dataStoreConfig.putIdHojaPrincipalPreference(0)
    }


    /** REALIZAR BALANCE E INCERTAR EN T_BALANCE AL CERRAR LA HOJA **/
    suspend fun instanciarInsertarBalance(){
        var balances : List<DbBalanceEntity> = listOf()
        calcularBalance()
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



    /** METODO QUE OBTIENE UNA HOJACONBALANCE DE LA BBDD Y CARGA AL INFO EN UNO DE LOS STATE **/
    fun getHojaConBalances() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hojaCalculoRepository.getHojaConBalances(gastosState.value.hojaAMostrar?.hoja!!.idHoja).collect{
                    _gastosState.value = _gastosState.value.copy(hojaConBalances = it)
                    calcularBalanceFinal()
                }
            }
        }
    }

    /** INSERTAR PAGO DE LA DEUDA **/
    fun pagarDeuda(acreedor: Pair<String, Double>?){
        if (acreedor != null) {
            viewModelScope.launch{
                calculoUpdatePago(acreedor)?.let {
                    val pagoInsertado = pagoRepository.insertPago(it)
                    if (pagoInsertado > 0) onPagoRealizadoChanged(true)
                }
            }
        }
    }

    /** INSTANCIA UNA ENTIDAD DE T_PAGOS **/
    suspend fun calculoUpdatePago(acreedor: Pair<String, Double>): DbPagoEntity? {
        val participantes = gastosState.value.hojaAMostrar?.participantes ?: return null
        val hojaConBalances = gastosState.value.hojaConBalances ?: return null
        val idRegistrado = gastosState.value.idRegistrado
        val fotoPago = gastosState.value.imageAbsolutePath

        val idPagador = participantes.firstOrNull {
            it.participante.idRegistroParti == idRegistrado
        }?.participante?.idParticipante ?: return null

        val montoAcreedorRedondeado = acreedor.second.redondearADosDecimales()
        val miDeuda = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }?.monto ?: return null
        val montoDeudaRedondeado = miDeuda.redondearADosDecimales()

        val balanceDeudor = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }
        val balanceAcreedor = hojaConBalances.balances.firstOrNull { it.monto == montoAcreedorRedondeado }

        val (nuevoMontoDeudor, montoPagado, montoAcreedorActualizado) = calcularNuevosMontos(montoDeudaRedondeado, montoAcreedorRedondeado)

        val idFotoPago = insertFoto(fotoPago)
        val actualizado = updateBalance(balanceDeudor, balanceAcreedor, nuevoMontoDeudor, montoAcreedorActualizado)

        return if (actualizado) {
            instanciarPago(balanceDeudor, balanceAcreedor, montoPagado, idFotoPago)
        } else {
            null
        }
    }

    fun Double.redondearADosDecimales(): Double {
        return BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun Double.esMontoPequeno(): Boolean {
        return (this.redondearADosDecimales() == -0.01 || this.redondearADosDecimales() == 0.01)
    }

    fun calcularNuevosMontos(deuda: Double, acreedor: Double): Triple<Double, Double, Double> {
        val resto = deuda + acreedor
        return if (resto < 0) {
            Triple(resto, acreedor, 0.0)
        } else {
            Triple(0.0, acreedor - resto, resto)
        }.let { (nuevoMontoDeudor, montoPagado, montoAcreedorActualizado) ->
            Triple(
                if (nuevoMontoDeudor.esMontoPequeno()) 0.0 else nuevoMontoDeudor,
                montoPagado,
                if (montoAcreedorActualizado.esMontoPequeno()) 0.0 else montoAcreedorActualizado
            )
        }
    }

    /** METODO QUE ACTUALIZA LA LINEA DE T_BALANCE **/
    suspend fun updateBalance(
        balanceDeudor: DbBalanceEntity?,
        balanceAcreedor: DbBalanceEntity?,
        nuevoMontoDeudor: Double,
        montoAcreedorRedondeado: Double
    ): Boolean {
        val exitoDeudor = balanceDeudor?.let {
            it.monto = nuevoMontoDeudor
            if (it.monto == 0.0) it.tipo = "B"
            balanceRepository.updateBalance(it)
        } ?: false

        val exitoAcreedor = balanceAcreedor?.let {
            it.monto = montoAcreedorRedondeado
            if (it.monto == 0.0) it.tipo = "B"
            balanceRepository.updateBalance(it)
        } ?: false

        return exitoDeudor && exitoAcreedor
    }

    /** METODO QUE INSTANCIA UNA ENTIDAD DE T_PAGO **/
    fun instanciarPago(
        balanceDeudor: DbBalanceEntity?,
        balanceAcreedor: DbBalanceEntity?,
        montoPagado: Double,
        idFotoPago: Long?
    ): DbPagoEntity{
        return DbPagoEntity(
            idPago = 0,
            idBalance = balanceDeudor!!.idBalance,
            idBalancePagado = balanceAcreedor!!.idBalance,
            monto = montoPagado,
            idFotoPago = idFotoPago,
            fechaPago = Validaciones.fechaToStringFormat(LocalDate.now())!!,
            fechaConfirmacion = ""
        )
    }

    /** METODO QUE INSERTA LA FOTO Y ACTUALIZA EL GASTO **/
    fun insertaFotoGasto(imagen: Uri){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val idFoto = insertFoto(imagen.toString())

                if (idFoto != null){
                    updateFoto(idFoto)
                }
            }
        }
    }

    /** INSERTAR IMAGEN EN LA BBDD **/
    suspend fun insertFoto(foto: String?): Long?{
        return foto?.let { DbFotoEntity(rutaFoto = it) }?.let { fotoRepository.insertFoto(it) }
    }

    /** OBTENER IMAGEN DE LA BBDD **/
    fun getFotoPago(idFoto: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val rutaFoto = fotoRepository.getFoto(idFoto).firstOrNull()?.rutaFoto
                if(rutaFoto != null) onImageUriChanged(Uri.parse(rutaFoto))
            }
        }
    }

    fun obtenerFotoPago(idFoto: Long){
         getFotoPago(idFoto)
    }

}