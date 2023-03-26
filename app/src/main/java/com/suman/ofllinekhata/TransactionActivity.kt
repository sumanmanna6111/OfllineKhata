package com.suman.ofllinekhata

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.suman.ofllinekhata.adapter.CustomerAdapter
import com.suman.ofllinekhata.adapter.TransactionAdapter
import com.suman.ofllinekhata.databinding.ActivityTransactionBinding
import com.suman.ofllinekhata.entity.CustomerEntity
import com.suman.ofllinekhata.entity.TransactionEntity
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.model.CustomerModel
import com.suman.ofllinekhata.model.TransactionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding
    var tranList: ArrayList<TransactionModel>? = null
    var adapter: TransactionAdapter? = null
    var id: Int = 0
    var name: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        tranList = ArrayList<TransactionModel>()
        adapter = TransactionAdapter(tranList!!)
        binding.listTransaction.setHasFixedSize(true)
        binding.listTransaction.layoutManager = LinearLayoutManager(this)
        binding.listTransaction.adapter = adapter
        id = intent.getIntExtra("id", 0)
        name = intent.getStringExtra("name") ?: ""

        adapter!!.setOnClickListener(object : OnClickListener {
            override fun onClick(id: Int, name: String) {
                startActivity(Intent(this@TransactionActivity, TransactionActivity::class.java).putExtra("id",id))
            }

        })
    }
    override fun onStart() {
        super.onStart()
        tranList?.clear()
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
            val uid = id
            val transactionDao = db.transactionDao()
            val transactions: List<TransactionEntity> = transactionDao.getCustomerTran(uid)
            val getCredit = transactionDao.getTotalCredit(uid)
            val getDebit = transactionDao.getTotalDebit(uid)
            val creditBalance =  "Due \u20B9${getCredit ?: 0.00f}"
            val debitBalance ="Advance \u20B9${getDebit ?: 0.00f}"
            MainScope().launch(Dispatchers.Default){
                binding.tvTotalCredit.text = creditBalance
                binding.tvTotalDebit.text = debitBalance
                binding.toolbar.title = name
            }
            for (transaction in transactions){
                tranList?.add(TransactionModel(transaction.id, transaction.amount, transaction.description, transaction.time))
            }
        }
        job.join()
        adapter?.notifyDataSetChanged()
    }
}