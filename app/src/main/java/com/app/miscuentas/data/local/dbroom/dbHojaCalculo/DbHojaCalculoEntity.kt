package com.app.miscuentas.data.local.dbroom.dbHojaCalculo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.miscuentas.domain.model.HojaCalculo
import java.time.LocalDate

@Entity(tableName = "t_hojas_cab")
class DbHojaCalculoEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "titulo") var titulo: String = "",
    @ColumnInfo(name = "fechaCierre") var fechaCierre: String?,
    @ColumnInfo(name = "limite") var limite: Double?,
    @ColumnInfo(name = "status") var status: String

)

fun DbHojaCalculoEntity.toDomain() = HojaCalculo(
    id = id,
    titulo = titulo,
    fechaCierre = fechaCierre,
    limite = limite,
    status = status
)

@Entity(tableName = "t_hojas_lin")
class DbHojaCalculoEntityLin (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "linea") var linea: Int = 0,
    @ColumnInfo(name = "id_participante") var id_participante: Int = 0,
    @ColumnInfo(name = "status_linea") var status_linea: String
)
