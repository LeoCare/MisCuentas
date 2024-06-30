package com.app.miscuentas.features.gastos

import android.net.Uri
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.domain.model.HojaCalculo

data class GastosState (
    val hojaPrincipal: HojaCalculo? = null,
    val hojaConBalances: HojaConBalances? = null,
    val idRegistrado: Long = 0,
    val existeRegistrado: Boolean = false,
    val hojaAMostrar: HojaConParticipantes? = null,
    val listaGastosOk: Boolean = false,
    val idHojaPrincipal: Long? = null,
    val gastoElegido: DbGastosEntity? = null,
    val cierreAceptado: Boolean = false,
    val sumaParticipantes: Map<String, Double>? = null, //suma de gastos por participante
    val balanceDeuda: Map<String, Double>? = null, //importe que debe/recibe cada participante hasta antes del cierre
    val pagoRealizado: Boolean = false,
    val permisoCamara: Boolean = false,
    var imageUri: Uri? = null,
    val imageAbsolutePath: String? = null,
    val imagenPago: String? = null,
    val listaPagosConParticipantes: List<PagoConParticipantes>? = listOf(),
    val permisoDenegadoPermanente: Boolean = false,
    val permisoState: PermissionState? = null
){
    sealed class PermissionState {
        object Concedido : PermissionState()
        object Denegado : PermissionState()
        object DenegPermanente : PermissionState()
    }
}