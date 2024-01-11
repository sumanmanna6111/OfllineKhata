package com.suman.ofllinekhata.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.viewmodel.LoginViewModel
import java.lang.IllegalArgumentException

class LoginViewModelFactory(private val database: AppDatabase, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}