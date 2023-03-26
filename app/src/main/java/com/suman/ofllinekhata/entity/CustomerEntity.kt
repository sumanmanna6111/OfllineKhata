package com.suman.ofllinekhata.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "customer" , indices = [Index(value = ["number"], unique = true)])
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "number") val number: String,
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "time") val time: Long?
)
