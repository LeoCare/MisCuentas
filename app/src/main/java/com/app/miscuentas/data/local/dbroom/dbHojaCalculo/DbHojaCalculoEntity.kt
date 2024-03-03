package com.app.miscuentas.data.local.dbroom.dbHojaCalculo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.app.miscuentas.data.local.dbroom.dbParticipantes.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.dbParticipantes.toDomain
import com.app.miscuentas.domain.model.HojaCalculo
import java.time.LocalDate

@Entity(
    tableName = "t_hojas_cab"
)
class DbHojaCalculoEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "titulo") var titulo: String = "",
    @ColumnInfo(name = "fechaCierre") var fechaCierre: String?,
    @ColumnInfo(name = "limite") var limite: Double?,
    @ColumnInfo(name = "status") var status: String,
    @ColumnInfo(name = "principal") var principal: String = "N",
)

fun DbHojaCalculoEntity.toDomain() = HojaCalculo(
    id = id,
    titulo = titulo,
    fechaCierre = fechaCierre,
    limite = limite,
    status = status,
    participantesHoja = null,
    principal = principal == "S"
)

@Entity(
    tableName = "t_hojas_lin",
    primaryKeys = ["id", "linea"],
    indices = [Index(value =  ["id", "id_participante"], unique = true)] )
class DbHojaCalculoEntityLin (
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "linea") var linea: Int = 0,
    @ColumnInfo(name = "id_participante") var id_participante: Int = 0,
    @ColumnInfo(name = "statusLinea") var statusLinea: String = "P"
)


@Entity(
    tableName = "t_hojas_lin_det",
    primaryKeys = ["id", "linea", "linea_detalle"],
    indices = [Index(value =  ["id", "linea_detalle"], unique = true)] )
class DbHojaCalculoEntityLinDet (
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "linea") var linea: Int = 0,
    @ColumnInfo(name = "linea_detalle") var linea_detalle: Int = 0,
    @ColumnInfo(name = "id_gasto") var id_gasto: Int = 0,
    @ColumnInfo(name = "concepto") var concepto: String = "",
    @ColumnInfo(name = "importe") var importe: Double = 0.0
)

