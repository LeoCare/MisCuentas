package com.app.miscuentas.data.local.dbroom.dbGastos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.HojaCalculo

@Entity(
    tableName = "t_gastos",
    primaryKeys = ["id"],
    indices = [Index(value =  ["id"])]
)
class DbGastosEntity (
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "concepto") var concepto: String = ""
)

//fun DbGastosEntity.toDomain() = Gasto(
//    id = id,
//    concepto = concepto,
//    importe = 0.0,
//    imagen = null,
//    _fechaGasto = null
//)


