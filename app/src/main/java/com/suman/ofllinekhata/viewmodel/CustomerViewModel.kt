package com.suman.ofllinekhata.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suman.ofllinekhata.repository.CustomerRepository
import com.suman.ofllinekhata.room.entity.CustomerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: CustomerRepository): ViewModel() {

    fun getCustomer(): LiveData<List<CustomerEntity>>{
        return repository.getAllCustomer()
    }
    fun getTotalCredit(): LiveData<Float?>{
        return repository.getTotalCredit()
    }
    fun getTotalDebit(): LiveData<Float?>{
        return repository.getTotalDebit()
    }
}