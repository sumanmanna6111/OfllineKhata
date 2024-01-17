package com.suman.ofllinekhata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suman.ofllinekhata.repository.AddCustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCustomerViewModel(private val repository: AddCustomerRepository): ViewModel() {

    fun addCustomer(name: String, number: String, amount: Float, type: Int, desc: String){
        viewModelScope.launch(Dispatchers.IO){
            repository.addCustomer(name, number, amount)
            repository.addTransaction(type,desc, amount)
        }
    }
}