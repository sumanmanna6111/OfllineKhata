package com.suman.ofllinekhata.repository

import androidx.lifecycle.LiveData
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity

class TransferRepository(private val database: AppDatabase) {
    private val customerDao = database.customerDao()
    private val transactionDao = database.transactionDao()
    suspend fun getAllCustomer() : List<CustomerEntity>{
        return customerDao.getAllCustomerT()
    }
    suspend fun getAllTransaction() : List<TransactionEntity>{
        return transactionDao.getAllTransactionT()
    }
    suspend fun insertCustomer(list: CustomerEntity){
        customerDao.insertAll(list)
    }
    suspend fun insertTransaction(list: TransactionEntity){
        transactionDao.insertAll(list)
    }
}