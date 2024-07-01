package com.app.miscuentas.features.balance

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbFotoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.data.local.repository.BalanceRepository
import com.app.miscuentas.data.local.repository.FotoRepository
import com.app.miscuentas.data.local.repository.GastoRepository
import com.app.miscuentas.data.local.repository.HojaCalculoRepository
import com.app.miscuentas.data.local.repository.PagoRepository
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

    fun onPagoRealizadoChanged(realizado: Boolean){
        _balanceState.value = _balanceState.value.copy(pagoRealizado = realizado)
    }
    fun onListaPagosConParticipantesChanged(listaPagosConParticipantes: List<PagoConParticipantes>){
        _balanceState.value = _balanceState.value.copy(listaPagosConParticipantes = listaPagosConParticipantes)
    }
    fun onImageAbsolutePathChanged(imageAbsolutePath: String?){
        _balanceState.value = _balanceState.value.copy(imageAbsolutePath = imageAbsolutePath)
    }
    fun onImageUriChanged(imageUri: Uri?){
        _balanceState.value = _balanceState.value.copy(imagenUri = imageUri)
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
        val participantes = balanceState.value.hojaAMostrar?.participantes ?: return null
        val hojaConBalances = balanceState.value.hojaConBalances ?: return null
        val idRegistrado = balanceState.value.idRegistrado
        val fotoPago = balanceState.value.imageAbsolutePath

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
        val hoja = balanceState.value.hojaAMostrar
        val hojaConBalances = balanceState.value.hojaConBalances

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
                pago.idFotoPago,
                (pago.fechaConfirmacion.isNotEmpty())
            )
            listPagosConParticipantes = listPagosConParticipantes + pagoConParticipantes

        }
        onListaPagosConParticipantesChanged(listPagosConParticipantes)
    }

    /** INSERTAR IMAGEN EN LA BBDD **/
    suspend fun insertFoto(foto: String?): Long?{

        return foto?.let { DbFotoEntity(rutaFoto = it) }?.let { fotoRepository.insertFoto(it) }
    }

    /** OBTENER IMAGEN DE LA BBDD **/
    suspend fun getFotoPago(idFoto: Long): String? {
        return fotoRepository.getFoto(idFoto).firstOrNull()?.rutaFoto
    }

    fun obtenerFotoPago(idFoto: Long) {
        viewModelScope.launch {
            val rutaFoto = getFotoPago(idFoto)
            onImageAbsolutePathChanged(rutaFoto)

        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun solicitaPermisos(statePermisosAlmacenamiento: MultiplePermissionsState)  {
        statePermisosAlmacenamiento.launchMultiplePermissionRequest()
    }


    fun getPathFromUri(context: Context, uri: Uri): String? {
        val isKitKat = true

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            when {
                isExternalStorageDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return "${Environment.getExternalStorageDirectory()}/${split[1]}"
                    }
                    // Handle non-primary volumes
                }
                isDownloadsDocument(uri) -> {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                }
                isMediaDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }

        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                              selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = uri?.let {
                context.contentResolver.query(it, projection, selection, selectionArgs,
                    null)
            }
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }



}