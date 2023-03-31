package com.suman.ofllinekhata

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.suman.ofllinekhata.databinding.ActivityDetailsBinding
import com.suman.ofllinekhata.databinding.ActivityTransactionBinding
import com.suman.ofllinekhata.entity.TransactionEntity
import com.suman.ofllinekhata.model.TransactionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    var id: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra("id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            getTranDetails()
        }

    }
    private suspend fun getTranDetails(){
        val job = CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "khata.db"
            ).build()
            val transactionDao = db.transactionDao()
            val transactions: TransactionEntity = transactionDao.getTranDetails(id)
            if(db.isOpen) {
                db.close()
            }


        }
        job.join()

    }
}