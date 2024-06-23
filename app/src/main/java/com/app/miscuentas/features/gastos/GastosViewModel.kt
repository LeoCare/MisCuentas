package com.app.miscuentas.features.gastos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.app.miscuentas.features.splash.SplashState
import com.app.miscuentas.util.Validaciones
import com.app.miscuentas.util.Contabilidad
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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
        _gastosState.value = _gastosState.value.copy(gastoElegido = gasto)
    }
    fun onResumenGastoChanged(mapaGastos: Map<String,Double>){
        _gastosState.value = _gastosState.value.copy(resumenGastos = mapaGastos)
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
    fun onPermisoCamaraChanged(permitido: Boolean){
        _gastosState.value = _gastosState.value.copy(permisoCamara = permitido)
    }
    fun onListaPagosConParticipantesChanged(listaPagosConParticipantes: List<PagoConParticipantes>){
        _gastosState.value = _gastosState.value.copy(listaPagosConParticipantes = listaPagosConParticipantes)
    }
    fun onImageUriChanged(image: Uri){
        _gastosState.value = _gastosState.value.copy(imageUri = image)
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
        val mapaResumen = Contabilidad.obtenerParticipantesYSumaGastos(hoja!!) as MutableMap<String, Double>
        onResumenGastoChanged(mapaResumen)
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
        getPagos()
    }


    /** METODO QUE ELIMINA LA LINEA DE T_GASTOS **/
    //Borrar
    suspend fun deleteGasto(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                gastoRepository.delete(gastosState.value.gastoElegido)
            }
        }
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
    suspend fun calculoUpdatePago(acreedor: Pair<String, Double>): DbPagoEntity?{
        //valores del State:
        val participantes = gastosState.value.hojaAMostrar?.participantes ?: return null
        val hojaConBalances = gastosState.value.hojaConBalances ?: return null
        val idRegistrado = gastosState.value.idRegistrado
//        //valores a pasar para instanciar:
        val idPagador = participantes.firstOrNull{
            it.participante.idRegistroParti == idRegistrado
        }?.participante?.idParticipante ?: return null
//
        var montoAcreedorRedondeado = BigDecimal(acreedor.second).setScale(2, RoundingMode.HALF_UP).toDouble()
        val miDeuda = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }?.monto ?: return null
        val montoDeudaRedondeado = BigDecimal(miDeuda).setScale(2, RoundingMode.HALF_UP).toDouble()

        val balanceDeudor = hojaConBalances.balances.firstOrNull { it.idParticipanteBalance == idPagador }
        val balanceAcreedor = hojaConBalances.balances.firstOrNull { it.monto == montoAcreedorRedondeado }


        val resto = montoDeudaRedondeado + montoAcreedorRedondeado
        var nuevoMontoDeudor = 0.0
        val montoPagado: Double

        if(resto < 0.0) { //si el resto es negativo, aun me queda deuda pendiente
            nuevoMontoDeudor = resto
            montoPagado = montoAcreedorRedondeado
            montoAcreedorRedondeado = 0.0
        }
        else {
            montoPagado = montoAcreedorRedondeado - resto
            montoAcreedorRedondeado = resto
        }

        val actualizado = updateBalance(balanceDeudor, balanceAcreedor, nuevoMontoDeudor, montoAcreedorRedondeado)

        return if (actualizado) {
            instanciarPago(balanceDeudor,balanceAcreedor,montoPagado)
        }
        else{
            null
        }
    }

    /** METODO QUE ACTUALIZA LA LINEA DE T_BALANCE **/
    suspend fun updateBalance(
        balanceDeudor: DbBalanceEntity?,
        balanceAcreedor: DbBalanceEntity?,
        nuevoMontoDeudor: Double,
        montoAcreedorRedondeado: Double
    ): Boolean
    {
        var exito = false
        if (balanceDeudor != null) {
            if(nuevoMontoDeudor == 0.0) balanceDeudor.tipo = "B"
            balanceDeudor.monto = nuevoMontoDeudor
            exito = balanceRepository.updateBalance(balanceDeudor)
        }
        if (balanceAcreedor != null && exito) {
            if(montoAcreedorRedondeado == 0.0) balanceAcreedor.tipo = "B"
            balanceAcreedor.monto = montoAcreedorRedondeado
            exito = balanceRepository.updateBalance(balanceAcreedor)
        }
        return exito
    }

    /** METODO QUE INSTANCIA UNA ENTIDAD DE T_PAGO **/
    fun instanciarPago(
        balanceDeudor: DbBalanceEntity?,
        balanceAcreedor: DbBalanceEntity?,
        montoPagado: Double
    ): DbPagoEntity{
        return DbPagoEntity(
            idPago = 0,
            idBalance = balanceDeudor!!.idBalance,
            idBalancePagado = balanceAcreedor!!.idBalance,
            monto = montoPagado,
            fechaPago = Validaciones.fechaToStringFormat(LocalDate.now())!!,
            fechaConfirmacion = ""
        )
    }

    /** METODO QUE OBTIENE UNA LISTA DE LOS PAGOS PARA UN BALANCE **/
    fun getPagos(){
        var listaPagos: List<DbPagoEntity> = listOf()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                gastosState.value.hojaConBalances?.balances?.forEach{
                    listaPagos = listaPagos + pagoRepository.getPagosByDeuda(it.idBalance)
                }
                pagosConParticipantes(listaPagos)
            }
        }
    }


    /** METODO QUE CREA UNA LISTA DE PAGOS CON PARTICIPANTES, SU MONTO Y FECHA **/
    fun pagosConParticipantes(listaPagos: List<DbPagoEntity>) {
        var listPagosConParticipantes: List<PagoConParticipantes> = listOf()
        var nombreDeudor = ""
        var nombreAcreedor = ""
        val hoja = gastosState.value.hojaAMostrar
        val hojaConBalances = gastosState.value.hojaConBalances

        listaPagos.forEach { pago ->
            hojaConBalances?.balances?.forEach { balance ->
                if (pago.idBalance == balance.idBalance) {
                    hoja?.participantes?.forEach { participantes ->
                        if (participantes.participante.idParticipante == balance.idParticipanteBalance) {
                            nombreDeudor = participantes.participante.nombre
                        }
                    }
                }
                if (pago.idBalancePagado == balance.idBalance) {
                    hoja?.participantes?.forEach { participantes ->
                        if (participantes.participante.idParticipante == balance.idParticipanteBalance) {
                            nombreAcreedor = participantes.participante.nombre
                        }
                    }
                }
            }
            val pagoConParticipantes = PagoConParticipantes(
                nombreDeudor,
                nombreAcreedor,
                pago.monto,
                pago.fechaPago,
                (pago.fechaConfirmacion.isNotEmpty())
            )
            listPagosConParticipantes = listPagosConParticipantes + pagoConParticipantes

        }
        onListaPagosConParticipantesChanged(listPagosConParticipantes)
    }

    /** INSERTAR IMAGEN EN LA BBDD **/
    fun insertFoto(photoPath: String) {
        viewModelScope.launch {
            fotoRepository.insertFoto(DbFotoEntity(photoPath = photoPath))
        }
    }


    @OptIn(ExperimentalPermissionsApi::class)
    fun solicitaPermisos(statePermisosAlmacenamiento: MultiplePermissionsState)  {
        statePermisosAlmacenamiento.launchMultiplePermissionRequest()
    }

}