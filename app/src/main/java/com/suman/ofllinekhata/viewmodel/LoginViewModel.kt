package com.suman.ofllinekhata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suman.ofllinekhata.helper.PrefManager


class LoginViewModel(private val application: Application): AndroidViewModel(application) {


    val _shopName = MutableLiveData<String>()
    val shopName: LiveData<String> get() = _shopName




}