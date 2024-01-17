package com.suman.ofllinekhata.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.suman.ofllinekhata.repository.AddCustomerRepository
import com.suman.ofllinekhata.viewmodel.AddCustomerViewModel

class AddCustomerViewModelFactory(private val repository: AddCustomerRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return AddCustomerViewModel(repository) as T
    }
}