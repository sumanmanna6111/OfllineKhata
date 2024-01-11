package com.suman.ofllinekhata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.suman.ofllinekhata.room.AppDatabase

class LoginViewModel(private val database: AppDatabase, private val application: Application): AndroidViewModel(application) {

}