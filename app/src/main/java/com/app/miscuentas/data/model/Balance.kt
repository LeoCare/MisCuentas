package com.app.miscuentas.data.model

import com.app.miscuentas.data.local.dbroom.entitys.DbBalancesEntity
import com.app.miscuentas.domain.dto.BalanceCrearDto
import com.app.miscuentas.domain.dto.BalanceDto

data class Balance(
    var idBalance: Long = 0,
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
    monto = monto.toDouble()
)

fun Balance.toDto() = BalanceDto(
    idBalance = idBalance,
    idHoja = idHojaBalance,
    idParticipante = idParticipanteBalance,
    tipo = tipo,
    monto = monto.toString().replace(',','.')
)

fun Balance.toCrearDto() = BalanceCrearDto(
    idHoja = idHojaBalance,
    idParticipante = idParticipanteBalance,
    tipo = tipo,
    monto = monto.toString().replace(',','.')
)

fun List<BalanceDto>.dtoToEntityList(): List<DbBalancesEntity> {
    return this.map { balanceDto ->
        balanceDto.toEntity()
    }
}

fun List<Balance>.toEntityList(): List<DbBalancesEntity> {
    return this.map { balance ->
        balance.toEntity()
    }
}