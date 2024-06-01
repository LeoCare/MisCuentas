package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.Participante

@Entity( tableName = "t_hojas_cab" )
data class DbHojaCalculoEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idHoja") var idHoja: Long = 0,
    @ColumnInfo(name = "titulo") var titulo: String = "",
    @ColumnInfo(name = "fechaCreacion") var fechaCreacion: String?,
    @ColumnInfo(name = "fechaCierre") var fechaCierre: String?,
    @ColumnInfo(name = "limite") var limite: String?,
    @ColumnInfo(name = "status") var status: String,
    @ColumnInfo(name = "principal") var principal: Boolean = false
)

fun DbHojaCalculoEntity.toDomain(participantes: List<Participante> = listOf()) = HojaCalculo(
    idHoja = idHoja,
    titulo = titulo,
    fechaCreacion = fechaCreacion,
    fechaCierre = fechaCierre,
    limite = limite,
    status = status,
    participantesHoja = participantes,
    principal = principal
)

//@Entity(
//    tableName = "t_hojas_lin",
//    primaryKeys = ["idHoja", "linea"],
//    indices = [Index(value =  ["idHoja", "id_participante"], unique = true)] )
//class DbHojaCalculoEntityLin (
//    @ColumnInfo(name = "idHoja") var idHoja: Int = 0,
//    @ColumnInfo(name = "linea") var linea: Int = 0,
//    @ColumnInfo(name = "id_participante") var id_participante: Int = 0,
//    @ColumnInfo(name = "statusLinea") var statusLinea: String = "P"
//)
//
//
//@Entity(
//    tableName = "t_hojas_lin_det",
//    primaryKeys = ["idHoja", "linea", "linea_detalle"])
//   // indices = [Index(value =  ["id", "linea_detalle"], unique = true)] )
//class DbHojaCalculoEntityLinDet (
//    @ColumnInfo(name = "idHoja") var idHoja: Int = 0,
//    @ColumnInfo(name = "linea") var linea: Int = 0,
//    @ColumnInfo(name = "linea_detalle") var linea_detalle: Int = 0,
//    @ColumnInfo(name = "id_gasto") var id_gasto: Int = 0,
//    @ColumnInfo(name = "concepto") var concepto: String = "",
//    @ColumnInfo(name = "importe") var importe: String?,
//    @ColumnInfo(name = "fecha_gasto") var fecha_gasto: String?
//)

