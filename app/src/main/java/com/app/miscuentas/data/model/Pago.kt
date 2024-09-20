package com.app.miscuentas.data.model

import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.domain.dto.PagoDto


data class Pago(
    val idPago: Long = 0,
    val idBalance: Long,
    val idBalancePagado: Long,
    val monto: Double,
    var idFotoPago: Long? = null,
    val fechaPago: String,
    val fechaConfirmacion: String,
)

fun Pago.toEntity() = DbPagoEntity(
     idPago = idPago,
     idBalance = idBalance,
     idBalancePagado = idBalancePagado,
     monto = monto,
     idFotoPago = idFotoPago,
     fechaPago = fechaPago,
     fechaConfirmacion = fechaConfirmacion,
)

fun PagoDto.toEntity() = DbPagoEntity(
    idPago = idPago,
    idBalance = idBalance,
    idBalancePagado = idBalancePagado,
    monto = monto,
    idFotoPago = idImagen,
    fechaPago = fechaPago,
    fechaConfirmacion = fechaConfirmacion.toString(),
)

fun List<PagoDto>.toEntityList(): List<DbPagoEntity> {
    return this.map { pagoDto ->
        pagoDto.toEntity()
    }
}
