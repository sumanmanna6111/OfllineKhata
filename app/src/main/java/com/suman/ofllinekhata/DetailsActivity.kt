package com.suman.ofllinekhata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.suman.ofllinekhata.databinding.ActivityDetailsBinding
import com.suman.ofllinekhata.entity.CustomerEntity
import com.suman.ofllinekhata.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    var id: Int = 0
    var amount: Float = 0f
    private var uid: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        id = intent.getIntExtra("id", 0)

        binding.btnSaveTran.setOnClickListener {
            if (binding.tranDetailIsClear.isChecked && binding.tranDetailIsClear.isClickable){
                saveTran()
            }else{
                Toast.makeText(this, "nothing change", Toast.LENGTH_SHORT).show()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            getTranDetails()
        }
    }

    private fun saveTran() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "khata.db"
            ).build()
            val transactionDao = db.transactionDao()
            val customerDao = db.customerDao()
            val clearTime: Long = System.currentTimeMillis()
            transactionDao.dueReceived(if (binding.tranDetailIsClear.isChecked) 1 else 0, clearTime, id)
            customerDao.update(balance = -amount, userid = uid)
            val prefManager = PrefManager(this@DetailsActivity)
            if (prefManager.getBoolean("sms")){
                val customer: CustomerEntity = customerDao.loadAllById(uid)
                val msgType: String = if (customer.amount <= 0){
                    Config.paidDue
                }else {
                    Config.paidAdv
                }
                val msg:String = String.format(msgType, customer.name, abs(amount), prefManager.getString("company"), abs(customer.amount) )
                SMSManager.sendSMS(customer.number, msg)
            }

            if(db.isOpen) {
                db.close()
            }
        }
        finish()
    }

    private suspend fun getTranDetails(){
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "khata.db"
            ).build()
            val transactionDao = db.transactionDao()
            val transactions: TransactionEntity = transactionDao.getTranDetails(id)
            amount = transactions.amount
            uid = transactions.uid
            if(db.isOpen) {
                db.close()
            }
            runOnUiThread {
                binding.tranDetailAmt.setText(abs(transactions.amount).toString())
                binding.tranDetailDesc.setText(transactions.description)
                if (transactions.type == 1) binding.btnBebit.isChecked = true else binding.btnCredit.isChecked = true
                binding.tranDetailIsClear.isChecked = transactions.received == 1
                binding.tranDetailIsClear.isClickable = transactions.received != 1
                binding.tranDetailClear.setText(
                    transactions.clear?.let { SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(Date(it) )})
                binding.tranDetailTime.setText(SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(Date(transactions.time)))
            }


        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}