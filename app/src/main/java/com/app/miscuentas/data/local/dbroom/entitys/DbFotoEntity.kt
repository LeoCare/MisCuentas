package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_fotos")
data class DbFotoEntity (
    @PrimaryKey(autoGenerate = true) val idFoto: Long = 0,
    @ColumnInfo(name = "imagen") val imagen: ByteArray
)