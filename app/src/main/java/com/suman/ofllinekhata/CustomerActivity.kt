package com.suman.ofllinekhata

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.suman.ofllinekhata.adapter.CustomerAdapter
import com.suman.ofllinekhata.databinding.ActivityCustomerBinding
import com.suman.ofllinekhata.entity.CustomerEntity
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.model.CustomerModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerBinding
    var customerList: ArrayList<CustomerModel>? = null
    var adapter: CustomerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customerList = ArrayList<CustomerModel>()
        adapter = CustomerAdapter(customerList!!)
        binding.listCustomer.setHasFixedSize(true)
        binding.listCustomer.layoutManager = LinearLayoutManager(this)
        binding.listCustomer.adapter = adapter

        adapter!!.setOnClickListener(object : OnClickListener{
            override fun onClick(id: Int) {
                //TODO("Not yet implemented")
                Log.d("TAG", "onClick: ")
            }

        })

        binding.btnAddCustomer.setOnClickListener {
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }


    }

    override fun onStart() {
        super.onStart()
        customerList?.clear()
        CoroutineScope(Dispatchers.Main).launch {
            getCustomer()
        }
    }

     @SuppressLint("NotifyDataSetChanged")
     private suspend fun getCustomer(){
         val job = CoroutineScope(Dispatchers.IO).launch {
             val db = Room.databaseBuilder(
                 applicationContext,
                 AppDatabase::class.java, "khata.db"
             ).build()
             val customerDao = db.customerDao()
             val customers: List<CustomerEntity> = customerDao.getAll()
             Log.d("TAG", "getCustomer: ${customerDao.getTotalCredit()}")
             for (customer in customers){
                 customerList!!.add(CustomerModel(customer.id, customer.name, customer.number, customer.amount))
             }

         }
         job.join()
         adapter!!.notifyDataSetChanged()
    }
}