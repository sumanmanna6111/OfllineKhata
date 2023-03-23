package com.suman.ofllinekhata

import androidx.room.Database
import androidx.room.RoomDatabase
import com.suman.ofllinekhata.dao.CustomerDao
import com.suman.ofllinekhata.dao.TransactionDao
import com.suman.ofllinekhata.entity.CustomerEntity
import com.suman.ofllinekhata.entity.TransactionEntity

@Database(entities = [CustomerEntity::class, TransactionEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
}