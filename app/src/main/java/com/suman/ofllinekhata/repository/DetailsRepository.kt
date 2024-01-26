package com.suman.ofllinekhata.repository

import androidx.lifecycle.LiveData
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity

class DetailsRepository(private val database: AppDatabase) {
    private val transactionDao = database.transactionDao()
    private val customerDao = database.customerDao()

    fun getTransactionDetails(id: Int): LiveData<TransactionEntity>{
        return transactionDao.getTranDetails(id)
    }

    suspend fun loadById(uid: Int): CustomerEntity{
        return customerDao.loadAllById(uid)
    }
    suspend fun transactionUpdate (clear: Int, clearTime: Long, id: Int){
        transactionDao.dueReceived(
            clear,
            clearTime,
            id
        )
    }

    suspend fun customerUpdate (amount: Float, uid: Int){
        customerDao.update(balance = -amount, userid = uid)
    }
}