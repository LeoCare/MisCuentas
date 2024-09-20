package com.app.miscuentas.data.model

import com.app.miscuentas.data.local.dbroom.entitys.DbBalancesEntity
import com.app.miscuentas.domain.dto.BalanceDto

data class Balance(
    val idBalance: Long = 0,
    val idHojaBalance: Long,
    val idParticipanteBalance: Long,
    var tipo: String,
    var monto: Double
)

fun Balance.toEntity() = DbBalancesEntity(
    idBalance = idBalance,
    idHojaBalance = idHojaBalance,
    idParticipanteBalance = idParticipanteBalance,
    tipo = tipo,
    monto = monto
)

fun BalanceDto.toEntity() = DbBalancesEntity(
    idBalance = idBalance,
    idHojaBalance = idHoja,
    idParticipanteBalance = idParticipante,
    tipo = tipo,
    monto = monto
)

fun List<BalanceDto>.toEntityList(): List<DbBalancesEntity> {
    return this.map { balanceDto ->
        balanceDto.toEntity()
    }
}