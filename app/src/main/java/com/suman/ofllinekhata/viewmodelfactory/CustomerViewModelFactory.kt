package com.suman.ofllinekhata.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suman.ofllinekhata.repository.CustomerRepository
import com.suman.ofllinekhata.viewmodel.CustomerViewModel

class CustomerViewModelFactory(private val repository: CustomerRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CustomerViewModel(repository) as T
    }
}