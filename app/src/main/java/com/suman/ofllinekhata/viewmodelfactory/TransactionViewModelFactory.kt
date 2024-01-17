package com.suman.ofllinekhata.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suman.ofllinekhata.repository.TransactionRepository
import com.suman.ofllinekhata.viewmodel.TransactionViewModel

class TransactionViewModelFactory(private val transactionRepository: TransactionRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TransactionViewModel(transactionRepository) as T
    }
}