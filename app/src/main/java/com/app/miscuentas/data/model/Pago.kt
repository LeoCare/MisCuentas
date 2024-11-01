package com.app.miscuentas.data.model

import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.dto.PagoCrearDto
import com.app.miscuentas.data.dto.PagoDto


data class Pago(
    val idPago: Long = 0,
    val idParticipantePago: Long,
    val idBalance: Long,
    val idBalancePagado: Long,
    val monto: Double,
    var idFotoPago: Long? = null,
    val fechaPago: String,
    val fechaConfirmacion: String? = null
)

fun Pago.toEntity() = DbPagoEntity(
     idPago = idPago,
     idParticipantePago = idParticipantePago,
     idBalance = idBalance,
     idBalancePagado = idBalancePagado,
     monto = monto,
     idFotoPago = idFotoPago,
     fechaPago = fechaPago,
     fechaConfirmacion = fechaConfirmacion
)

fun PagoDto.toEntity() = DbPagoEntity(
    idPago = idPago,
    idParticipantePago = idParticipante,
    idBalance = idBalance,
    idBalancePagado = idBalancePagado,
    monto = monto,
    idFotoPago = idImagen,
    fechaPago = fechaPago,
    fechaConfirmacion = fechaConfirmacion
)

fun Pago.toCrearDto() = PagoCrearDto(
    idParticipante = idParticipantePago,
    idBalance = idBalance,
    idBalancePagado = idBalancePagado,
    monto = monto.toString(),
    idImagen = idFotoPago,
    fechaPago = fechaPago,
    fechaConfirmacion = fechaConfirmacion
)

fun DbPagoEntity.toDto() = PagoDto(
    idParticipante = idParticipantePago,
    idPago = idPago,
    idBalance = idBalance,
    idBalancePagado = idBalancePagado,
    monto = monto,
    idImagen  = idFotoPago,
    fechaPago = fechaPago,
    fechaConfirmacion = fechaConfirmacion.toString()
)

fun List<PagoDto>.toEntityList(): List<DbPagoEntity> {
    return this.map { pagoDto ->
        pagoDto.toEntity()
    }
}
