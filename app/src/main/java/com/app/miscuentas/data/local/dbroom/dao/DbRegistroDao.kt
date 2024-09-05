package com.app.miscuentas.data.local.dbroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbRegistrosEntity
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.Registro
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityWithRegistro
import kotlinx.coroutines.flow.Flow

@Dao
interface DbRegistroDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg registro: DbRegistrosEntity)


    @Transaction
    suspend fun insertRegistroConParticipante(registro: DbRegistrosEntity, participante: DbParticipantesEntity) : Long{
        val idRegistro = insert(registro)
        val participanteEntity = participante.copy(idRegistroParti = idRegistro)
        insertParticipante(participanteEntity)
        return idRegistro
    }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(registro: DbRegistrosEntity): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParticipante(participante: DbParticipantesEntity): Long

    @Update
    suspend fun update(registro: DbRegistrosEntity)

    @Delete
    suspend fun delete(registro: DbRegistrosEntity)

    //Obtener usuario resgistrado al olvidar la contraseña
    @Query("SELECT * FROM t_registros WHERE idRegistro = :idRegistro")
    fun getRegistroWhereId(idRegistro: Long): Flow<DbRegistrosEntity?>

    //Obtener usuario registrado segun nombre y contraseña
    @Query("SELECT * FROM t_registros WHERE nombre = :nombre AND contrasenna = :contrasenna")
    fun getRegistroWhereLogin(nombre: String, contrasenna: String): Flow<DbRegistrosEntity?>

    //Obtener usuario resgistrado al olvidar la contraseña
    @Query("SELECT * FROM t_registros WHERE correo = :correo")
    fun getRegistroWhereCorreo(correo: String): Flow<DbRegistrosEntity?>



}