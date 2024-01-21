package com.suman.ofllinekhata.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.callback.CompletedCallback
import com.koushikdutta.async.callback.DataCallback
import com.koushikdutta.async.http.WebSocket
import com.koushikdutta.async.http.WebSocket.StringCallback
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.suman.ofllinekhata.repository.CustomerRepository
import com.suman.ofllinekhata.room.entity.CustomerEntity
import java.lang.Exception
import kotlin.coroutines.coroutineContext
import kotlin.math.log


class CustomerViewModel(private val repository: CustomerRepository): ViewModel() {
    companion object{

        private const val TAG = "CustomerViewModel"
    }
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