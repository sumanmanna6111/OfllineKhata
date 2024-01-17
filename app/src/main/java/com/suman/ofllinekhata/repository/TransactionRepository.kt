package com.suman.ofllinekhata.repository

import androidx.lifecycle.LiveData
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity

class TransactionRepository(private val database: AppDatabase) {
    private val customerDao = database.customerDao()
    private val transactionDao = database.transactionDao()

    fun getCustomerTran(uid: Int): LiveData<List<TransactionEntity>>{
        return transactionDao.getCustomerTran(uid)
    }

    fun getTotal(uid: Int): LiveData<Float?>{
        return transactionDao.getTotal(uid)
    }

    suspend fun addTransaction(uid: Int, type: Int, description: String, amount: Float) {
        val time: Long = System.currentTimeMillis()
        transactionDao.insertAll(
            TransactionEntity(
                0,
                uid,
                type,
                description,
                amount,
                0,
                null,
                time
            )
        )
    }

    suspend fun updateBalance(amount: Float, uid: Int){
        customerDao.update(balance = amount, userid = uid)
    }

}