package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import kotlinx.coroutines.flow.Flow

@Dao
interface DbParticipantesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg participante: DbParticipantesEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(participante: DbParticipantesEntity): Long

    @Transaction
    suspend fun cleanInsert(participante: DbParticipantesEntity): Long {
        clearAllParticipantes()
        return insert(participante)
    }

    //Como la entidad representa una fila en concreto, si se le pasa la entidad modificada la actualizara en la BBDD
    @Update
    suspend fun update(participante: DbParticipantesEntity)

    @Update
    suspend fun updateParticipanteConGasto(participante: DbParticipantesEntity)

    @Delete
    suspend fun delete(participante: DbParticipantesEntity)

    @Query("DELETE FROM t_participantes")
    suspend fun clearAllParticipantes()

    @Transaction
    @Query("SELECT * FROM t_participantes p, t_hojas_cab h WHERE p.idUsuarioParti = :idRegistro AND h.idUsuarioHoja = :idRegistro")
    fun getParticipanteConGastos(idRegistro: Long): Flow<ParticipanteConGastos>

    @Query("SELECT * FROM t_participantes WHERE idParticipante = :id")
    fun getParticipante(id: Int): Flow<DbParticipantesEntity>

    @Query("SELECT * FROM t_participantes ORDER BY idParticipante DESC")
    fun getAllParticipantes(): Flow<List<DbParticipantesEntity>>


    @Query("SELECT idParticipante FROM t_participantes WHERE nombre = :nombre ORDER BY idParticipante DESC")
    fun getIdParticipante(nombre: String): Int

    @Query("SELECT MAX(idParticipante) FROM t_participantes")
    fun getMaxIdParticipantes(): Int

}