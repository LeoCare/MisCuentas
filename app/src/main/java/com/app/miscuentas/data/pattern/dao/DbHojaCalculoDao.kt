package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
//import com.app.miscuentas.data.local.dbroom.entitys.DbHojaParticipantesGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface DbHojaCalculoDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHojaCalculo( hojaCalculo: DbHojaCalculoEntity): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParticipante(participante: DbParticipantesEntity): Long


    @Update
    suspend fun updateHoja(hojaCalculo: DbHojaCalculoEntity)

    @Delete
    suspend fun delete(hojaCalculo: DbHojaCalculoEntity)

    @Query("DELETE FROM t_hojas_cab")
    suspend fun clearAllHojas()

    //Room mantiene el Flow actualizado, por lo que solo se necesita obtener los datos una vez.
    //Luego Room se encarga de notificarnos con cada cambio en los datos
    @Query("SELECT * FROM t_hojas_cab WHERE idHoja = :id")
    fun getHojaCalculo(id: Long): Flow<DbHojaCalculoEntity>

    @Query("SELECT * FROM t_hojas_cab ORDER BY idHoja DESC")
    fun getAllHojasCalculos(): Flow<List<DbHojaCalculoEntity>>

    @Transaction
    @Query("SELECT * FROM t_hojas_cab ORDER BY idHoja DESC")
    fun getAllHojaConParticipantes(): Flow<List<HojaConParticipantes>>

    @Transaction
    @Query("SELECT * FROM t_hojas_cab ORDER BY idHoja DESC")
    fun getHojasConParticipantes(): List<HojaConParticipantes>

    @Transaction
    @Query("SELECT * FROM t_hojas_cab ORDER BY idHoja DESC")
    fun getHojasConParticipantesFlow(): Flow<List<HojaConParticipantes>>

    @Transaction
    @Query("SELECT * FROM t_hojas_cab WHERE idUsuarioHoja = :idUsuario ORDER BY idHoja DESC")
    fun getAllHojaConParticipantes(idUsuario: Long): Flow<List<HojaConParticipantes>>

    //Obtener el ID de la ultima hoja creada para la insercion en t_hojas_cab_lin
    @Query("SELECT MAX(idHoja) FROM t_hojas_cab")
    fun getMaxIdHojasCalculos(): Flow<Long>

    //PARA INSERTAR DOS CLASES RELACIONADAS DEBEN INSERTARSE EN UNA MISMA TRANSACCION:
    @Transaction
    suspend fun insertHojaConParticipantes(hoja: DbHojaCalculoEntity, participantes: List<DbParticipantesEntity>) {
        val hojaId = insertHojaCalculo(hoja)
        participantes.forEach { participante ->
            val participanteConHojaId = participante.copy(idHojaParti = hojaId)
            insertParticipante(participanteConHojaId)
        }
    }

    @Transaction
    suspend fun deleteHojaConParticipantes(hojaCalculo: DbHojaCalculoEntity) {
        delete(hojaCalculo)
    }

    @Transaction
    @Query("SELECT * FROM t_hojas_cab WHERE idHoja = :id")
    fun getHojaConParticipantes(id: Long): Flow<HojaConParticipantes?>

    @Transaction
    @Query("SELECT COUNT(idHoja) FROM t_hojas_cab")
    fun getTotalHojasCreadas(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM t_hojas_cab WHERE idHoja = :id")
    fun getHojaConBalances(id: Long): Flow<HojaConBalances?>


//    @Transaction
//    @Query("SELECT * FROM t_hojas_cab")
//    fun getHojaConParticipantesGastos(): List<DbHojaParticipantesGastosEntity>

    /*
    //Obtener el valor de la linea del pagador (de la hoja especificada) para la insercion en t_hojas_cab_lin_det
    @Query("SELECT MAX(linea) FROM t_hojas_lin WHERE id = :id ORDER BY linea DESC")
    fun getMaxLineaHojasCalculos(id: Int): Flow<Int>

    //Obtener el valor de la linea del pagador (de la hoja especificada) para la insercion en t_hojas_cab_lin_det
    @Query("SELECT linea FROM t_hojas_lin WHERE id = :id AND id_participante = :idParticipante")
    fun getLineaPartiHojasCalculosLin(id: Int, idParticipante: Int): Flow<Int>

    //Obtener el valor de la ultima linea detalle creada (de la hoja y linea especificada) para la insercion en t_hojas_cab_lin_det
    @Query("SELECT MAX(linea_detalle) FROM t_hojas_lin_det WHERE id = :id AND linea = :linea")
    fun getMaxLineaDetHojasCalculos(id: Int, linea: Int): Flow<Int?>

    @Query("SELECT * FROM t_hojas_lin_det WHERE id = :id AND linea = :idParticipante")
    fun getGastos(id: Int, idParticipante: Int): Flow<List<DbGastosEntity>>

    @Query("SELECT hld.id_gasto, hld.importe, hld.concepto, hld.fecha_gasto " +
            "FROM t_hojas_lin_det hld, t_hojas_lin hl " +
            "WHERE hl.id = :id " +
            "AND hl.id_participante = :idParticipante " +
            "AND hld.id = hl.id " +
            "AND hld.linea = hl.linea")
    fun getGastosParticipante(id: Int, idParticipante: Int): Flow<List<Gasto?>>

    @Query("SELECT * FROM t_hojas_lin_det ORDER BY id DESC")
    fun getAllGastos(): Flow<List<DbGastosEntity>>
    */

}