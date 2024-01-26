package com.suman.ofllinekhata.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suman.ofllinekhata.repository.TransactionRepository
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TransactionViewModel(private val transactionRepository: TransactionRepository): ViewModel() {

    fun getTransaction(uid: Int): LiveData<List<TransactionEntity>>{
        return transactionRepository.getCustomerTran(uid)
    }
    fun getTotal(uid: Int): LiveData<Float?>{
        return transactionRepository.getTotal(uid)
    }

    fun addTransaction(uid: Int, type: Int, description: String, amount: Float){
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.addTransaction(uid, type, description, amount)
        }
    }

    fun updateBalance(amount: Float, uid: Int){
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.updateBalance(amount, uid)
        }
    }

    suspend fun getByUid(uid: Int): CustomerEntity{
        val job = viewModelScope.async {
            transactionRepository.getByUid(uid)
        }
        return job.await()
    }
}