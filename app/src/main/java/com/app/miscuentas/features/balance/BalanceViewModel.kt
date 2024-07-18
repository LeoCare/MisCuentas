package com.app.miscuentas.features.balance

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbFotoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.data.local.repository.BalanceRepository
import com.app.miscuentas.data.local.repository.FotoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.PagoRepository
import com.app.miscuentas.util.Contabilidad
import com.app.miscuentas.util.Contabilidad.Contable.esMontoPequeno
import com.app.miscuentas.util.Contabilidad.Contable.redondearADosDecimales
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
class BalanceViewModel @Inject constructor(
    private val hojaCalculoRepository: HojaCalculoRepository,
    private val balanceRepository: BalanceRepository,
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
    fun onImagenBitmapChanged(imagenBitmap: Bitmap?){
        _balanceState.value = _balanceState.value.copy(imagenBitmap = imagenBitmap)
    }
    fun onHojaAMostrarChanged(hoja: HojaConParticipantes?){
        _balanceState.value = _balanceState.value.copy(hojaAMostrar = hoja)
    }
    fun onHojaConBalanceChanged(hojaConBalances: HojaConBalances?){
        _balanceState.value = _balanceState.value.copy(hojaConBalances = hojaConBalances)
    }
    fun onNewFotoPagoChanged(pago: PagoConParticipantes?){
        _balanceState.value = _balanceState.value.copy(pagoNewFoto = pago)
    }
    fun onMostrarFotoChanged(mostrar: Boolean){
        _balanceState.value = _balanceState.value.copy(mostrarFoto = mostrar)
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
                        calcularBalance()
                    }
                }
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
                    calculoFinal()
                }
            }
        }
    }
    /** METODO QUE CALCULA EL BALANCE AL FINALIZAR LA HOJA **/
    private fun calculoFinal(){
        val hoja = balanceState.value.hojaAMostrar
        val hojaConBalances = balanceState.value.hojaConBalances
        val balanceDeuda = Contabilidad.calcularBalanceFinal(hoja, hojaConBalances)
        onBalanceDeudaChanged(balanceDeuda)
        getPagos()
    }

    /** INSERTAR PAGO DE LA DEUDA **/
    fun pagarDeuda(deudor: Pair<String, Double>?, acreedor: Pair<String, Double>?){
        if (acreedor != null) {
            viewModelScope.launch{
                calculoUpdatePago(deudor, acreedor)?.let {
                    val pagoInsertado = pagoRepository.insertPago(it)
                    if (pagoInsertado > 0) { //si el pago se ha insertado
                        onPagoRealizadoChanged(true)
                        updateIfHojaBalanceada() //compruebo si esta balanciado en su totalidad
                    }
                }
            }

        }
    }

    /** INSTANCIA UNA ENTIDAD DE T_PAGOS **/
    suspend fun calculoUpdatePago(deudor: Pair<String, Double>?, acreedor: Pair<String, Double>): DbPagoEntity? {
        val participantes = balanceState.value.hojaAMostrar?.participantes ?: return null
        val hojaConBalances = balanceState.value.hojaConBalances ?: return null
        val imagenPago = balanceState.value.imagenBitmap

        val idPagador = participantes.firstOrNull {
            it.participante.nombre == deudor?.first
        }?.participante?.idParticipante ?: return null

        val montoAcreedorRedondeado = acreedor.second.redondearADosDecimales()
        val deuda = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }?.monto ?: return null
        val montoDeudaRedondeado = deuda.redondearADosDecimales()

        val balanceDeudor = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }
        val balanceAcreedor = hojaConBalances.balances.firstOrNull { it.monto == montoAcreedorRedondeado }

        val (nuevoMontoDeudor, montoPagado, montoAcreedorActualizado) = Contabilidad.calcularNuevosMontos(montoDeudaRedondeado, montoAcreedorRedondeado)

        val idFotoPago = if(imagenPago != null) insertImage(imagenPago) else null
        val actualizado = updateBalance(balanceDeudor, balanceAcreedor, nuevoMontoDeudor, montoAcreedorActualizado)

        return if (actualizado) {
            instanciarPago(balanceDeudor, balanceAcreedor, montoPagado, idFotoPago)
        } else {
            null
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

    /** METODO QUE ACTUALIZA LA FOTO DEL  PAGO **/
    suspend fun updatePagoWithFoto(idFoto: Long){
        val idPago = balanceState.value.pagoNewFoto?.idPago
        val pagoEntity = idPago?.let {
            pagoRepository.getPagosById(it)
        }
        if (pagoEntity != null) {
            pagoEntity.idFotoPago = idFoto
            pagoRepository.updatePago(pagoEntity)
        }
    }

    /** METODO QUE ACTUALIZA LA LISTA DE PAGOSCONPARTICIPANTES AL CAMBIAR DE FOTO **/
    fun updateListaPagoConParticipantes(foto: Bitmap?){
        val idPagoConParticipante = balanceState.value.pagoNewFoto?.idPago

        balanceState.value.listaPagosConParticipantes?.firstOrNull {
            it.idPago == idPagoConParticipante
        }?.fotoPago = foto
         //onMostrarFotoChanged(true)
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
    /** esto se hace para tener una lista con los nombres y la foto **/
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
                imagenBitmap = pago.idFotoPago?.let { obtenerFotoPago(it) }
            }
            val pagoConParticipantes = PagoConParticipantes(
                pago.idPago,
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
    suspend fun insertImage(bitmap: Bitmap): Long {
        val byteArray = bitmapToByteArray(bitmap,50)
        val imageEntity = DbFotoEntity(imagen = byteArray)
        return fotoRepository.insertFoto(imageEntity)
    }

    /** METODO QUE INSERTA LA FOTO Y ACTUALIZA EL GASTO **/
    fun insertNewImage(bitmap: Bitmap) {
        onImagenBitmapChanged(bitmap)
        val byteArray = bitmapToByteArray(bitmap, 50)
        val imageEntity = DbFotoEntity(imagen = byteArray)
        viewModelScope.launch {
            val idFoto = fotoRepository.insertFoto(imageEntity)
            withContext(Dispatchers.IO) { updatePagoWithFoto(idFoto) }
            withContext(Dispatchers.Main){
                updateListaPagoConParticipantes(bitmap)
                onNewFotoPagoChanged(null)
            }
        }
    }

    /** OBTENER IMAGEN DE LA BBDD **/
    fun obtenerFotoPago(idFoto: Long): Bitmap {
        val imagenByteArray = fotoRepository.getFoto(idFoto).imagen
        return byteArrayToBitmap(imagenByteArray)
    }

}