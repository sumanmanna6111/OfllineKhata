package com.suman.ofllinekhata.repository

import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity

class AddCustomerRepository(private val database: AppDatabase) {
    private val transactionDao = database.transactionDao()
    private val customerDao = database.customerDao()

    suspend fun addCustomer(name: String, number: String, amount: Float){
        val time: Long = System.currentTimeMillis()
        customerDao.insertAll(CustomerEntity(0, name, number, amount, time))
    }

    suspend fun addTransaction(type: Int, description: String, amount: Float){
        val time: Long = System.currentTimeMillis()
        val uid = customerDao.getLastUser() ?: 1
        transactionDao.insertAll(
            TransactionEntity(0, uid, type, description, amount, 0, null, time)
        )
    }

}