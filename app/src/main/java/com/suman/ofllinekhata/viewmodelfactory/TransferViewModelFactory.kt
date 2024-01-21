package com.suman.ofllinekhata.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suman.ofllinekhata.repository.TransactionRepository
import com.suman.ofllinekhata.repository.TransferRepository
import com.suman.ofllinekhata.viewmodel.TransferViewModel

class TransferViewModelFactory(private val repository: TransferRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TransferViewModel(repository) as T
    }
}