package com.suman.ofllinekhata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import com.suman.ofllinekhata.databinding.ActivityAddCustomerBinding
import com.suman.ofllinekhata.entity.CustomerEntity
import com.suman.ofllinekhata.entity.TransactionEntity
import com.suman.ofllinekhata.model.CustomerModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddCustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCustomerBinding
    var type: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.dueType.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.btn_credit ->{
                    type = 0
                }
                R.id.btn_bebit ->{
                    type = 1
                }
            }
        }
        binding.btnAddCustomer.setOnClickListener {
            addCustomer()
        }
    }
     private fun addCustomer(){
         val name: String = binding.edCustomerName.text.toString()
         val number: String = binding.edCustomerNumber.text.toString()
         var amount: Float = binding.edCustomerAmount.text.toString().toFloat()
         val description: String = binding.edCustomerDesc.text.toString()
         val time: Long = System.currentTimeMillis()

         CoroutineScope(Dispatchers.IO).launch {
             val db = Room.databaseBuilder(
                 applicationContext,
                 AppDatabase::class.java, "khata.db"
             ).build()
             val customerDao = db.customerDao()
             val transactionDao = db.transactionDao()
             if (type == 0) amount = -amount
             customerDao.insertAll(CustomerEntity(0, name, number, amount, time))
             transactionDao.insertAll(TransactionEntity(0,1, type, description, amount, time ))
             finish()
         }
     }
}