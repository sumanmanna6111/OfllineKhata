package com.suman.ofllinekhata.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tran")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "uid") val uid: Int?,
    @ColumnInfo(name = "type") val type: Int?,
    @ColumnInfo(name = "desc") val description: String?,
    @ColumnInfo(name = "amount") val amount: Float?,
    @ColumnInfo(name = "received") val received: Int?,
    @ColumnInfo(name = "clear") val clear: Long?,
    @ColumnInfo(name = "time") val time: Long?
)
