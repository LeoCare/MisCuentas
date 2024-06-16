package com.app.miscuentas.features.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.repository.BalanceRepository
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.PagoRepository
import com.app.miscuentas.domain.Validaciones
import com.app.miscuentas.util.Contabilidad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val pagoRepository: PagoRepository
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

    /** METODO QUE CLACULA EL BALANCE AL FINALIZAR LA HOJA **/
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


    /** ELIMINAR GASTO **/
    //Borrar
    suspend fun deleteGasto(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                gastoRepository.delete(gastosState.value.gastoElegido)
            }
        }
    }


    /** ACTUALIZAR HOJA **/
    //Actualizar
    suspend fun update() = viewModelScope.launch{
        onCierreAceptado(false)
        _gastosState.value.hojaAMostrar?.hoja?.status = "F"
        withContext(Dispatchers.IO) {
            hojaCalculoRepository.update(gastosState.value.hojaAMostrar?.hoja!!)
            instanciarInsertarBalance()
        }
    }

    //Metodo que actualiza el preference del idHoja a 0 al ya no estar Activa.
    suspend fun updatePreferenceIdHojaPrincipal() {
        dataStoreConfig.putIdHojaPrincipalPreference(0)
    }


    /** INSERTAR DEUDAS AL CERRAR LA HOJA **/
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
        viewModelScope.launch{
            instanciarPagoDeuda(acreedor)?.let {
                pagoRepository.insertPago(it)
            }
        }
    }

    /** INSTANCIA UNA ENTIDAD DE T_PAGOS **/
    fun instanciarPagoDeuda(acreedor: Pair<String, Double>?): DbPagoEntity? {
        //valores del State:
        val participantes = gastosState.value.hojaAMostrar?.participantes
        val hojaConBalances = gastosState.value.hojaConBalances
        val idRegistrado = gastosState.value.idRegistrado
        //valores a pasar para instanciar:
        val montoRedondeado = BigDecimal(acreedor!!.second).setScale(2, RoundingMode.HALF_UP).toDouble()
        val idPagador = participantes?.first{ it.participante.idRegistroParti == idRegistrado }?.participante?.idParticipante
        val idBalance = hojaConBalances?.balances?.firstOrNull { it.idParticipanteBalance == idPagador }?.idBalance
        val idBalancePagado = hojaConBalances?.balances?.firstOrNull { it.monto == montoRedondeado }?.idBalance

        return if(idBalance != null && idBalancePagado != null ){
            DbPagoEntity(
                idPago = 0,
                idBalance = idBalance,
                idBalancePagado = idBalancePagado,
                monto = montoRedondeado,
                fechaPago = Validaciones.fechaToStringFormat(LocalDate.now())!!,
                fechaConfirmacion = ""
            )
        } else null
    }

}