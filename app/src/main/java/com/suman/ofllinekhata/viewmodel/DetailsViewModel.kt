package com.suman.ofllinekhata.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suman.ofllinekhata.repository.DetailsRepository
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(private val detailsRepository: DetailsRepository): ViewModel() {

    fun getTransaction(id: Int): LiveData<TransactionEntity>{
            return detailsRepository.getTransactionDetails(id)
    }
    suspend fun getCustomer(uid: Int): CustomerEntity{
        return detailsRepository.loadById(uid)
    }

    fun tranUpdate(clear: Int, id: Int){
        viewModelScope.launch {
            val clearTime: Long = System.currentTimeMillis()
            detailsRepository.transactionUpdate(clear, clearTime, id)
        }
    }

    fun customerUpdate(amount: Float, uid: Int){
        viewModelScope.launch {
            detailsRepository.customerUpdate(amount, uid)
        }
    }

}