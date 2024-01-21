package com.suman.ofllinekhata.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suman.ofllinekhata.repository.TransferRepository
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TransferViewModel(private val repository: TransferRepository): ViewModel() {
    val _status = MutableLiveData<String>()
    suspend fun getAllCustomer(): List<CustomerEntity>{
        val job = CoroutineScope(Dispatchers.IO).async {
            repository.getAllCustomer()
        }
        return job.await()
    }

    suspend fun getAllTransaction(): List<TransactionEntity>{
        val job = CoroutineScope(Dispatchers.IO).async {
            repository.getAllTransaction()
        }
        return job.await()
    }

    fun insertCustomer(list: CustomerEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCustomer(list)
        }
    }
    fun insertTransaction(list: TransactionEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTransaction(list)
        }
    }

}