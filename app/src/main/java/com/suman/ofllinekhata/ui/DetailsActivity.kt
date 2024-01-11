package com.suman.ofllinekhata.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.helper.Config
import com.suman.ofllinekhata.helper.PrefManager
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.helper.SMSManager
import com.suman.ofllinekhata.databinding.ActivityDetailsBinding
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity
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
    var msg: String = ""
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        prefManager = PrefManager(this)
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
            val db = AppDatabase.getDataBase(applicationContext)
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
            val db = AppDatabase.getDataBase(applicationContext)
            val transactionDao = db.transactionDao()
            val customerDao = db.customerDao()

            val transactions: TransactionEntity = transactionDao.getTranDetails(id)
            uid = transactions.uid
            amount = transactions.amount
            val customer = customerDao.loadAllById(uid)
            if(db.isOpen) {
                db.close()
            }
            val totalAmt: Float = customer.amount
            val msgType: String = if (transactions.type == 0) {
                if (totalAmt <= 0) {
                    Config.purchaseDue
                } else {
                    Config.purchaseAdv
                }
            } else {
                if (totalAmt <= 0) {
                    Config.paidDue
                } else {
                    Config.paidAdv
                }
            }

            msg = String.format(
                msgType,
                customer.name,
                abs(amount),
                prefManager.getString("company"),
                abs(totalAmt)
            )

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tran_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.receipt -> {}
            R.id.share -> {
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}