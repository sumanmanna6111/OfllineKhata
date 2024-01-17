package com.suman.ofllinekhata.repository

import androidx.lifecycle.LiveData
import com.suman.ofllinekhata.room.dao.CustomerDao
import com.suman.ofllinekhata.room.entity.CustomerEntity

class CustomerRepository(private val dao : CustomerDao) {

    fun getAllCustomer(): LiveData<List<CustomerEntity>>{
        return dao.getAll()
    }

    fun getTotalCredit(): LiveData<Float?>{
        return dao.getTotalCredit()
    }

    fun getTotalDebit(): LiveData<Float?>{
        return dao.getTotalDebit()
    }

}