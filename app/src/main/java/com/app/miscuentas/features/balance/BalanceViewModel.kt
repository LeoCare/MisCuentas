package com.app.miscuentas.features.balance

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.dto.EmailDto
import com.app.miscuentas.data.dto.GastoCrearDto
import com.app.miscuentas.data.dto.GastoDto
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbBalancesEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbFotosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.data.model.toCrearDto
import com.app.miscuentas.data.model.toDto
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityList
import com.app.miscuentas.data.network.BalancesService
import com.app.miscuentas.data.network.EmailsService
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.ImagenesService
import com.app.miscuentas.data.network.PagosService
import com.app.miscuentas.util.Contabilidad
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
    private val hojasService: HojasService,
    private val balancesService: BalancesService,
    private val pagosService: PagosService,
    private val imagenesService: ImagenesService,
    private val emailsService: EmailsService,
    private val dataStoreConfig: DataStoreConfig
): ViewModel() {

    val _balanceState = MutableStateFlow(BalanceState())
    val balanceState: StateFlow<BalanceState> = _balanceState

    fun onIdRegistradoChanged(id: Long){
        _balanceState.value = _balanceState.value.copy(idRegistrado = id)
    }
    fun onIdPartiRegistradoChanged(id: Long){
        _balanceState.value = _balanceState.value.copy(idPartiRegistrado = id)
    }
    fun onBalanceDeudaChanged(mapaDeuda: Map<DbParticipantesEntity,Double>){
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
    fun onOpcionSelectedChanged(opcionElegida: String){
        _balanceState.value = _balanceState.value.copy(opcionSelected = opcionElegida)
    }
    fun onPagoAModificarChanged(pago: PagoConParticipantes){
        _balanceState.value = _balanceState.value.copy(pagoAConfirmar = pago)
    }


    //Metodo que obtiene el idRegistro de la DataStore y actualiza dicho State
    suspend fun getIdRegistroPreference() {
        val idRegistro = dataStoreConfig.getIdRegistroPreference()
        if (idRegistro != null) {
            onIdRegistradoChanged(idRegistro)
        }
    }

    /** METODO QUE OBTIENE UNA HOJACONPARTICIPANTES DE LA BBDD:
     * Posteriormetne comprueba si existe un usuario que sea el registrado.
     * Ademas, calcula el Balance final. **/
    fun onHojaAMostrar(idHoja: Long?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if (idHoja != null) {
                    hojasService.getHojaConParticipantes(idHoja).collect { hojaCalculo ->
                        //Obtengo el idParticipante del registrado
                        val idPartiRegistrado = hojaCalculo?.participantes?.first { parti ->
                            parti.participante.idUsuarioParti == balanceState.value.idRegistrado
                        }?.participante?.idParticipante
                        if(idPartiRegistrado != null)  onIdPartiRegistradoChanged(idPartiRegistrado)

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
       val mapaDeuda = Contabilidad.calcularBalance(hoja!!) as MutableMap<DbParticipantesEntity, Double>
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
                hojasService.getHojaConBalances(balanceState.value.hojaAMostrar?.hoja!!.idHoja).collect{
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
    fun pagarDeuda(deudor: Pair<DbParticipantesEntity, Double>?, acreedor: Pair<DbParticipantesEntity, Double>?){
        if (acreedor != null) {
            viewModelScope.launch{
                calculoUpdatePago(deudor, acreedor)?.let {
                    try{
                        //Insert Pago desde Api
                        val pagoApi = pagosService.createPagoAPI(it.toDomain().toCrearDto())

                        pagoApi?.let { pago ->
                            //Insert Pago Room
                            val pagoInsertado = pagosService.insertPago(pago.toEntity())
                            if (pagoInsertado > 0) { //si el pago se ha insertado
                                insertEmail(pago.idBalance, pago.monto) //inserta linea para el envio automatico del email.
                                updateIfHojaBalanceada() //compruebo si esta balanceado en su totalidad

                            }
                        }
                    }catch (e: Exception) {
                        onPendienteSubirCambiosChanged(true) //algo a fallado en las solicitudes
                    }
                }
            }
        }
    }

    /** INSTANCIA UNA ENTIDAD DE T_PAGOS **/
    suspend fun calculoUpdatePago(deudor: Pair<DbParticipantesEntity, Double>?, acreedor: Pair<DbParticipantesEntity, Double>): DbPagoEntity? {
        val participantes = balanceState.value.hojaAMostrar?.participantes ?: return null
        val hojaConBalances = balanceState.value.hojaConBalances ?: return null
        val imagenPago = balanceState.value.imagenBitmap

        val idPagador = participantes.firstOrNull {
            it.participante == deudor?.first
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
            instanciarPago(idPagador ,balanceDeudor, balanceAcreedor, montoPagado, idFotoPago)
        } else {
            null
        }
    }


    /** METODO QUE ACTUALIZA LA LINEA DE T_BALANCE **/
    suspend fun updateBalance(
        balanceDeudor: DbBalancesEntity?,
        balanceAcreedor: DbBalancesEntity?,
        nuevoMontoDeudor: Double,
        montoAcreedorRedondeado: Double
    ): Boolean {
        try {
            if (balanceDeudor != null && balanceAcreedor != null) {
                //Ajustes deudor:
                balanceDeudor.monto = nuevoMontoDeudor
                if (balanceDeudor.monto == 0.0) balanceDeudor.tipo = "B"
                //Ajustes Acreedor:
                balanceAcreedor.monto = montoAcreedorRedondeado
                if (balanceAcreedor.monto == 0.0) balanceAcreedor.tipo = "B"


                //Update Balances desde Api
                val balanceDeudorApi = balancesService.putBalanceApi(balanceDeudor.toDomain().toDto())
                val balanceAcreedorApi =
                    balancesService.putBalanceApi(balanceAcreedor.toDomain().toDto())

                if (balanceDeudorApi != null && balanceAcreedorApi != null) {
                    //Update Balances Room
                    balancesService.updateBalance(balanceDeudor)
                    balancesService.updateBalance(balanceAcreedor)

                } else return false
            }
            return true
        }catch (e: Exception) {
            return false
        }
    }


    /** METODO QUE  COMPRUEBA Y ACTUALIZA EL ESTADO, EN CASO DE QUE LA HOJA ESTE BALANCEADA **/
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
                        try{
                            //Update Hoja desde Api
                            hojasService.updateHojaApi(hojaActualizada.toDomain().toDto())

                            //Update Hoja Room
                            hojasService.updateHoja(hojaActualizada)
                        }catch (e: Exception) {
                            onPendienteSubirCambiosChanged(true) //algo a fallado en las solicitudes
                        }
                    }
                }
            }
        }
        onPagoRealizadoChanged(true)
        getPagos()
    }

    /** METODO QUE INSERTA LOS DATOS PARA EL SERVICIO DE ENVIO DE EMAIL **/
    suspend fun insertEmail(idBalance: Long, monto: Double){
        val tipoEmail = if(monto > 0.0) "E" else "S"
        val emailDto = EmailDto(idBalance, tipoEmail, null, "P")
        try{
            val emailApi = emailsService.createEmailApi(emailDto)
        }catch (e: Exception) {
            null // inserción NOK
        }
    }


    /** METODO QUE ACTUALIZA LA FOTO DEL  PAGO **/
    suspend fun updatePagoWithFoto(idFoto: Long){
        val idPago = balanceState.value.pagoNewFoto?.idPago
        val pagoEntity = idPago?.let {
            pagosService.getPagosById(it)
        }
        if (pagoEntity != null) {
            pagoEntity.idFotoPago = idFoto
            pagosService.updatePago(pagoEntity)
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
        idParticipantePago: Long,
        balanceDeudor: DbBalancesEntity?,
        balanceAcreedor: DbBalancesEntity?,
        montoPagado: Double,
        idFotoPago: Long?
    ): DbPagoEntity {
        return DbPagoEntity(
            idPago = 0,
            idParticipantePago = idParticipantePago,
            idBalance = balanceDeudor!!.idBalance,
            idBalancePagado = balanceAcreedor!!.idBalance,
            monto = montoPagado,
            idFotoPago = idFotoPago,
            fechaPago = Validaciones.fechaToStringFormat(LocalDate.now())!!,
            fechaConfirmacion = null
        )
    }

    /** METODO QUE OBTIENE UNA LISTA DE LOS PAGOS PARA UN BALANCE **/
    fun getPagos(){
        var listaPagos: List<DbPagoEntity> = listOf()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                balanceState.value.hojaConBalances?.balances?.forEach{
                    listaPagos = listaPagos + pagosService.getPagosByDeuda(it.idBalance)
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
        var idDeudor: Long = 0
        var idAcreedor: Long = 0
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
                            idDeudor = participantes.participante.idParticipante
                        }
                    }
                }
                if (pago.idBalancePagado == balance.idBalance) { //Obtengo Acreedor
                    hoja?.participantes?.forEach { participantes ->
                        if (participantes.participante.idParticipante == balance.idParticipanteBalance) {
                            nombreAcreedor = participantes.participante.nombre
                            idAcreedor = participantes.participante.idParticipante
                        }
                    }
                }
                imagenBitmap = pago.idFotoPago?.let { obtenerFotoPago(it) }
            }
            val pagoConParticipantes = PagoConParticipantes(
                pago.idPago,
                idDeudor,
                idAcreedor,
                nombreDeudor,
                nombreAcreedor,
                pago.monto,
                pago.fechaPago,
                imagenBitmap,
                pago.fechaConfirmacion
            )
            listPagosConParticipantes = listPagosConParticipantes + pagoConParticipantes
        }
        onListaPagosConParticipantesChanged(listPagosConParticipantes)
    }

    /** INSERTAR IMAGEN EN LA BBDD **/
    suspend fun insertImage(bitmap: Bitmap): Long {
        val byteArray = bitmapToByteArray(bitmap,50)
        val imageEntity = DbFotosEntity(imagen = byteArray)
        return imagenesService.insertFoto(imageEntity)
    }

    /** METODO QUE INSERTA LA FOTO Y ACTUALIZA EL GASTO **/
    fun insertNewImage(bitmap: Bitmap) {
        onImagenBitmapChanged(bitmap)
        val byteArray = bitmapToByteArray(bitmap, 50)
        val imageEntity = DbFotosEntity(imagen = byteArray)
        viewModelScope.launch {
            val idFoto = imagenesService.insertFoto(imageEntity)
            withContext(Dispatchers.IO) { updatePagoWithFoto(idFoto) }
            withContext(Dispatchers.Main){
                updateListaPagoConParticipantes(bitmap)
                onNewFotoPagoChanged(null)
            }
        }
    }

    /** OBTENER IMAGEN DE LA BBDD **/
    fun obtenerFotoPago(idFoto: Long): Bitmap {
        val imagenByteArray = imagenesService.getFoto(idFoto).imagen
        return byteArrayToBitmap(imagenByteArray)
    }

    /** Pendiente subir cambios a la red **/
    fun onPendienteSubirCambiosChanged(pendiente: Boolean){
        _balanceState.value = _balanceState.value.copy(pendienteSubirCambios = pendiente)
    }

    /** CONFIRMACION DEL PAGO RECIBIDO **/
    suspend fun ConfirmarPago() {
        val idPago = balanceState.value.pagoAConfirmar?.idPago

        idPago?.let {
            withContext(Dispatchers.IO) {
                try {
                    val pago = pagosService.getPagosById(it)
                    pago.fechaConfirmacion = Validaciones.fechaToStringFormat(LocalDate.now()) ?: LocalDate.now().toString()
                    pagosService.updatePago(pago)
                    pagosService.updatePagoAPI(pago.toDto())
                } catch (e: Exception) {
                    onPendienteSubirCambiosChanged(true) //algo a fallado en las solicitudes
                }
            }
        }
    }

}