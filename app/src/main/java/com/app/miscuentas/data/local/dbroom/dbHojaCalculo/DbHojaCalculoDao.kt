package com.app.miscuentas.data.local.dbroom.dbHojaCalculo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.dbParticipantes.DbParticipantesEntity
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.toEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DbHojaCalculoDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllHojaCalculo( hojaCalculo: DbHojaCalculoEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllHojaCalculoLin( hojaCalculoLin: DbHojaCalculoEntityLin)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllHojaCalculoLinDet( hojaCalculoLinDet: DbHojaCalculoEntityLinDet)

    //Como la entidad representa una fila en concreto, si se le pasa la entidad modificada la actualizara en la BBDD
    @Update
    suspend fun update(hojaCalculo: DbHojaCalculoEntity)

    @Delete
    suspend fun delete(hojaCalculo: DbHojaCalculoEntity)

    //Room mantiene el Flow actualizado, por lo que solo se necesita obtener los datos una vez.
    //Luego Room se encarga de notificarnos con cada cambio en los datos
    @Query("SELECT * FROM t_hojas_cab WHERE id = :id")
    fun getHojaCalculo(id: Int): Flow<DbHojaCalculoEntity>

    @Query("SELECT * FROM t_hojas_cab ORDER BY id DESC")
    fun getAllHojasCalculos(): Flow<List<DbHojaCalculoEntity>>

    //Obtener la hoja principal
    @Query("SELECT * FROM t_hojas_cab WHERE principal = 'S'")
    fun getHojaCalculoPrincipal(): Flow<DbHojaCalculoEntity?>

    //Obtener el ID de la ultima hoja creada para la insercion en t_hojas_cab_lin
    @Query("SELECT MAX(id) FROM t_hojas_cab")
    fun getMaxIdHojasCalculos(): Flow<Int>

    //Obtener el valor de la linea del pagador (de la hoja especificada) para la insercion en t_hojas_cab_lin_det
    @Query("SELECT MAX(linea) FROM t_hojas_lin WHERE id = :id ORDER BY linea DESC")
    fun getMaxLineaHojasCalculos(id: Int): Flow<Int>

    //Obtener el valor de la linea del pagador (de la hoja especificada) para la insercion en t_hojas_cab_lin_det
    @Query("SELECT linea FROM t_hojas_lin WHERE id = :id AND id_participante = :idParticipante")
    fun getLineaPartiHojasCalculosLin(id: Int, idParticipante: Int): Flow<Int>

    //Obtener el valor de la ultima linea detalle creada (de la hoja y linea especificada) para la insercion en t_hojas_cab_lin_det
    @Query("SELECT MAX(linea_detalle) FROM t_hojas_lin_det WHERE id = :id AND linea = :linea")
    fun getMaxLineaDetHojasCalculos(id: Int, linea: Int): Flow<Int?>

}