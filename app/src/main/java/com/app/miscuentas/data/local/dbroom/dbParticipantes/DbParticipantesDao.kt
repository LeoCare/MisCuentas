package com.app.miscuentas.data.local.dbroom.dbParticipantes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DbParticipantesDao {
    //Una instancia de entity es una fila de la BBDD, si se intenta modificar desde alguna otra parte del codigo se pueden generar conflictos.
    //Con esta anotacion se puede indicar que hacer en caso de conflicto.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg participante: DbParticipantesEntity)

    //Como la entidad representa una fila en concreto, si se le pasa la entidad modificada la actualizara en la BBDD
    @Update
    suspend fun update(participante: DbParticipantesEntity)

    @Delete
    suspend fun delete(participante: DbParticipantesEntity)

    //Room mantiene el Flow actualizado, por lo que solo se necesita obtener los datos una vez.
    //Luego Room se encarga de notificarnos con cada cambio en los datos
    @Query("SELECT * FROM t_participantes WHERE id = :id")
    fun getParticipante(id: Int): Flow<DbParticipantesEntity>

    @Query("SELECT * FROM t_participantes ORDER BY id DESC")
    fun getAllParticipantes(): Flow<List<DbParticipantesEntity>>
}