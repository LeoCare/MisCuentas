package com.app.miscuentas.data.local.dbroom.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class DbFotoEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoPath: String
)