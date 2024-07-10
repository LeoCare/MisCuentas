package com.app.miscuentas.features.balance

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
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
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.data.local.repository.BalanceRepository
import com.app.miscuentas.data.local.repository.FotoRepository
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.PagoRepository
import com.app.miscuentas.domain.model.toEntity
import com.app.miscuentas.util.Contabilidad
import com.app.miscuentas.util.Imagen.Companion.bitmapToByteArray
import com.app.miscuentas.util.Imagen.Companion.byteArrayToBitmap
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
class BalanceViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    private val hojaCalculoRepository: HojaCalculoRepository,
    private val balanceRepository: BalanceRepository,
    private val gastoRepository: GastoRepository,
    private val pagoRepository: PagoRepository,
    private val fotoRepository: FotoRepository
): ViewModel() {

    val _balanceState = MutableStateFlow(BalanceState())
    val balanceState: StateFlow<BalanceState> = _balanceState

    fun onBalanceDeudaChanged(mapaDeuda: Map<String,Double>){
        _balanceState.value = _balanceState.value.copy(balanceDeuda = mapaDeuda)
    }
    fun onPagoRealizadoChanged(realizado: Boolean){
        _balanceState.value = _balanceState.value.copy(pagoRealizado = realizado)
    }
    fun onListaPagosConParticipantesChanged(listaPagosConParticipantes: List<PagoConParticipantes>){
        _balanceState.value = _balanceState.value.copy(listaPagosConParticipantes = listaPagosConParticipantes)
    }
    fun onImageUriChanged(imageUri: Uri?){
        _balanceState.value = _balanceState.value.copy(imagenUri = imageUri)
    }
    fun onHojaAMostrarChanged(hoja: HojaConParticipantes?){
        _balanceState.value = _balanceState.value.copy(hojaAMostrar = hoja)
    }
    fun onIdRegistradoChanged(idRegistrado: Long){
        _balanceState.value = _balanceState.value.copy(idRegistrado = idRegistrado)
    }
    fun onHojaConBalanceChanged(hojaConBalances: HojaConBalances?){
        _balanceState.value = _balanceState.value.copy(hojaConBalances = hojaConBalances)
    }

    /** METODO QUE OBTIENE UNA HOJACONPARTICIPANTES DE LA BBDD:
     * Posteriormetne comprueba si existe un usuario que sea el registrado.
     * Ademas, calcula el Balance final. **/
    fun onHojaAMostrar(idHoja: Long?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if (idHoja != null) {
                    hojaCalculoRepository.getHojaConParticipantes(idHoja).collect { hojaCalculo ->
                        onHojaAMostrarChanged(hojaCalculo)
                        onIdRegistradoChanged(dataStoreConfig.getIdRegistroPreference()!!)
                        comprobarExisteRegistrado()
                        calcularBalance()
                    }
                }
            }
        }
    }

    /** METODO QUE COMPRUEBA SI EN LA HOJA EXISTE UN PARTICIPANTE PARA EL REGISTRADO **/
    fun comprobarExisteRegistrado(){
        val hoja = balanceState.value.hojaAMostrar
        hoja?.participantes?.forEach {
            if(it.participante.idRegistroParti == balanceState.value.idRegistrado ){
                _balanceState.value = _balanceState.value.copy(existeRegistrado = true)
                return
            }
        }
    }

    /** METODO QUE CALCULA EL BALANCE:
     * Si la hoja esta finalizada el balance se obtiene de la BBDD
     * **/
    fun calcularBalance() {
        val hoja = balanceState.value.hojaAMostrar
        val mapaDeuda = Contabilidad.calcularBalance(hoja!!) as MutableMap<String, Double>
        onBalanceDeudaChanged(mapaDeuda)
        //si esta finalizada...
        if (balanceState.value.hojaAMostrar?.hoja?.status != "C") {
            getHojaConBalances() //..obtengo una HojaConBalance y modifico el mapa(nombre, monto) desde t_balance
        }
    }

    /** METODO QUE OBTIENE UNA HOJACONBALANCE DE LA BBDD Y CARGA AL INFO EN UNO DE LOS STATE **/
    fun getHojaConBalances() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hojaCalculoRepository.getHojaConBalances(balanceState.value.hojaAMostrar?.hoja!!.idHoja).collect{
                    onHojaConBalanceChanged(it)
                    calcularBalanceFinal()
                }
            }
        }
    }
    /** METODO QUE CALCULA EL BALANCE AL FINALIZAR LA HOJA **/
    fun calcularBalanceFinal(){
        val hoja = balanceState.value.hojaAMostrar
        val hojaConBalances = balanceState.value.hojaConBalances
        val balanceActual = balanceState.value.balanceDeuda
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
        getPagos()
    }

    /** INSERTAR PAGO DE LA DEUDA **/
    fun pagarDeuda(acreedor: Pair<String, Double>?){
        if (acreedor != null) {
            viewModelScope.launch{
                calculoUpdatePago(acreedor)?.let {
                    val pagoInsertado = pagoRepository.insertPago(it)
                    if (pagoInsertado > 0) { //si el pago se ha insertado
                        onPagoRealizadoChanged(true)
                    }
                }
            }
            updateIfHojaBalanceada()
        }
    }

    fun pagarDeuda2(deudor: Pair<String, Double>?, acreedor: Pair<String, Double>?){
        if (acreedor != null) {
            viewModelScope.launch{
                calculoUpdatePago2(deudor, acreedor)?.let {
                    val pagoInsertado = pagoRepository.insertPago(it)
                    if (pagoInsertado > 0) { //si el pago se ha insertado
                        onPagoRealizadoChanged(true)
                    }
                }
            }
            updateIfHojaBalanceada()
        }
    }

    /** INSTANCIA UNA ENTIDAD DE T_PAGOS **/
    suspend fun calculoUpdatePago(acreedor: Pair<String, Double>): DbPagoEntity? {
        val participantes = balanceState.value.hojaAMostrar?.participantes ?: return null
        val hojaConBalances = balanceState.value.hojaConBalances ?: return null
        val idRegistrado = balanceState.value.idRegistrado

        val idPagador = participantes.firstOrNull {
            it.participante.idRegistroParti == idRegistrado
        }?.participante?.idParticipante ?: return null

        val montoAcreedorRedondeado = acreedor.second.redondearADosDecimales()
        val miDeuda = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }?.monto ?: return null
        val montoDeudaRedondeado = miDeuda.redondearADosDecimales()

        val balanceDeudor = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }
        val balanceAcreedor = hojaConBalances.balances.firstOrNull { it.monto == montoAcreedorRedondeado }

        val (nuevoMontoDeudor, montoPagado, montoAcreedorActualizado) = calcularNuevosMontos(montoDeudaRedondeado, montoAcreedorRedondeado)

        val idFotoPago = 0L
        val actualizado = updateBalance(balanceDeudor, balanceAcreedor, nuevoMontoDeudor, montoAcreedorActualizado)

        return if (actualizado) {
            instanciarPago(balanceDeudor, balanceAcreedor, montoPagado, idFotoPago)
        } else {
            null
        }
    }

    suspend fun calculoUpdatePago2(deudor: Pair<String, Double>?, acreedor: Pair<String, Double>): DbPagoEntity? {
        val participantes = balanceState.value.hojaAMostrar?.participantes ?: return null
        val hojaConBalances = balanceState.value.hojaConBalances ?: return null

        val idPagador = participantes.firstOrNull {
            it.participante.nombre == deudor?.first
        }?.participante?.idParticipante ?: return null

        val montoAcreedorRedondeado = acreedor.second.redondearADosDecimales()
        val deuda = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }?.monto ?: return null
        val montoDeudaRedondeado = deuda.redondearADosDecimales()

        val balanceDeudor = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }
        val balanceAcreedor = hojaConBalances.balances.firstOrNull { it.monto == montoAcreedorRedondeado }

        val (nuevoMontoDeudor, montoPagado, montoAcreedorActualizado) = calcularNuevosMontos(montoDeudaRedondeado, montoAcreedorRedondeado)

        val idFotoPago = 0L
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
        return (this == -0.01 || this  == 0.01)
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
                if (montoAcreedorActualizado.redondearADosDecimales().esMontoPequeno()) 0.0 else montoAcreedorActualizado.redondearADosDecimales()
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


    /** METODO QUE  COMPRUEBA Y ACTUALIZA SI LA HOJA ESTA BALANCEADA **/
    fun updateIfHojaBalanceada(){
        val hojaActualizada = balanceState.value.hojaConBalances?.hoja

        if(hojaActualizada != null && hojaActualizada.status != "B"){
            var todoBalanceado = true
            balanceState.value.hojaConBalances?.balances?.forEach {
                if (it.tipo != "B"){
                    todoBalanceado = false
                }
            }
            if(todoBalanceado){
                hojaActualizada.status = "B"
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        hojaCalculoRepository.updateHoja(hojaActualizada)
                    }
                }
            }
        }
    }


    /** METODO QUE INSTANCIA UNA ENTIDAD DE T_PAGO **/
    fun instanciarPago(
        balanceDeudor: DbBalanceEntity?,
        balanceAcreedor: DbBalanceEntity?,
        montoPagado: Double,
        idFotoPago: Long?
    ): DbPagoEntity {
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

    /** METODO QUE OBTIENE UNA LISTA DE LOS PAGOS PARA UN BALANCE **/
    fun getPagos(){
        var listaPagos: List<DbPagoEntity> = listOf()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                balanceState.value.hojaConBalances?.balances?.forEach{
                    listaPagos = listaPagos + pagoRepository.getPagosByDeuda(it.idBalance)
                    pagosConParticipantes(listaPagos)
                }
            }
        }
    }


    /** METODO QUE CREA UNA LISTA DE PAGOS CON PARTICIPANTES, SU MONTO Y FECHA **/
    fun pagosConParticipantes(listaPagos: List<DbPagoEntity>) {
        var listPagosConParticipantes: List<PagoConParticipantes> = listOf()
        var nombreDeudor = ""
        var nombreAcreedor = ""
        var imagenBitmap: Bitmap? = null
        val hoja = balanceState.value.hojaAMostrar
        val hojaConBalances = balanceState.value.hojaConBalances

        listaPagos.forEach { pago ->
            hojaConBalances?.balances?.forEach { balance ->
                if (pago.idBalance == balance.idBalance) { //Obtengo Deudor
                    hoja?.participantes?.forEach { participantes ->
                        if (participantes.participante.idParticipante == balance.idParticipanteBalance) {
                            nombreDeudor = participantes.participante.nombre
                        }
                    }
                }
                if (pago.idBalancePagado == balance.idBalance) { //Obtengo Acreedor
                    hoja?.participantes?.forEach { participantes ->
                        if (participantes.participante.idParticipante == balance.idParticipanteBalance) {
                            nombreAcreedor = participantes.participante.nombre
                        }
                    }
                }
                imagenBitmap = obtenerFotoPago(pago.idFotoPago!!)
            }
            val pagoConParticipantes = PagoConParticipantes(
                nombreDeudor,
                nombreAcreedor,
                pago.monto,
                pago.fechaPago,
                imagenBitmap,
                (pago.fechaConfirmacion.isNotEmpty())
            )
            listPagosConParticipantes = listPagosConParticipantes + pagoConParticipantes

        }
        onListaPagosConParticipantesChanged(listPagosConParticipantes)
    }

    /** INSERTAR IMAGEN EN LA BBDD **/
    fun insertImage(bitmap: Bitmap) {
        val byteArray = bitmapToByteArray(bitmap)
        val imageEntity = DbFotoEntity(imagen = byteArray)
        viewModelScope.launch {
            fotoRepository.insertFoto(imageEntity)
        }
    }

    /** OBTENER IMAGEN DE LA BBDD **/
    fun obtenerFotoPago(idFoto: Long): Bitmap {
        val imagenByteArray = fotoRepository.getFoto(idFoto).imagen
        return byteArrayToBitmap(imagenByteArray)
    }

}