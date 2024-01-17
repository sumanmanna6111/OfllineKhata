package com.suman.ofllinekhata.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suman.ofllinekhata.repository.DetailsRepository
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.viewmodel.DetailsViewModel

class DetailsViewModelFactory(private val detailsRepository: DetailsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsViewModel(detailsRepository) as T
    }
}