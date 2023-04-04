package com.suman.ofllinekhata

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.suman.ofllinekhata.adapter.TransactionAdapter
import com.suman.ofllinekhata.databinding.ActivityTransactionBinding
import com.suman.ofllinekhata.entity.TransactionEntity
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.model.TransactionModel
import kotlinx.coroutines.*
import java.lang.Runnable

class TransactionActivity : AppCompatActivity() {
    private val TAG = "TransactionActivity"
    private lateinit var binding: ActivityTransactionBinding
    var tranList: ArrayList<TransactionModel>? = null
    var adapter: TransactionAdapter? = null
    var uid: Int = 0
    var name: String = ""
    var number: String = ""
    var type: Int = 0
    lateinit var mAmount: EditText
    lateinit var mDescription: EditText
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tranList = ArrayList<TransactionModel>()
        adapter = TransactionAdapter(tranList!!)
        binding.listTransaction.setHasFixedSize(true)
        binding.listTransaction.layoutManager = LinearLayoutManager(this)
        binding.listTransaction.adapter = adapter
        uid = intent.getIntExtra("id", 0)
        name = intent.getStringExtra("name") ?: ""
        number = intent.getStringExtra("number") ?: ""
        binding.toolbar.title = name

        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<TextView>(R.id.ic_close)
        val dueType = view.findViewById<RadioGroup>(R.id.due_type)
        mAmount = view.findViewById<EditText>(R.id.ed_customer_amount)
        mDescription = view.findViewById<EditText>(R.id.ed_customer_desc)
        val addBtn = view.findViewById<Button>(R.id.btn_add_tran)
        
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dueType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_credit -> {
                    type = 0
                }
                R.id.btn_bebit -> {
                    type = 1
                }
            }
        }
        addBtn.setOnClickListener {
            if (mAmount.text.isEmpty()) {
                mAmount.setError("Enter Amount")
            }else{
                CoroutineScope(Dispatchers.Main).launch {
                    addRecord()
                }
                dialog.dismiss()
            }

        }
        dialog.setCancelable(true)
        dialog.setContentView(view)

        adapter!!.setOnClickListener(object : OnClickListener {
            override fun onClick(id: Int, name: String, number:String) {
                startActivity(Intent(this@TransactionActivity, DetailsActivity::class.java).putExtra("id",id))
            }
        })

        binding.btnAddTran.setOnClickListener {
            dialog.show()
        }
    }
    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            getCustomer()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun getCustomer(){
        tranList?.clear()
        val job = CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "khata.db"
            ).build()
            val transactionDao = db.transactionDao()
            val transactions: List<TransactionEntity> = transactionDao.getCustomerTran(uid)
            val getTotal = transactionDao.getTotal(uid)
            val balance =  "Total \u20B9${getTotal ?: 0.00f}"
            if(db.isOpen) {
                db.close()
            }
            runOnUiThread(Runnable {
                binding.tvTotalBal.text = balance
                 })
            for (transaction in transactions){
                tranList?.add(TransactionModel(transaction.id, transaction.amount, transaction.description, transaction.time))
            }
        }
        job.join()
        runOnUiThread{adapter?.notifyDataSetChanged()}

    }

    private suspend fun addRecord() {
        var amount: Float = mAmount.text.toString().toFloat()
        val description: String = mDescription.text.toString()
        val time: Long = System.currentTimeMillis()

        val job = CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "khata.db"
            ).build()
            val customerDao = db.customerDao()
            val transactionDao = db.transactionDao()
            if (type == 0)amount = -amount
            try {
                customerDao.update(amount, uid)
                var totalAmt: Float = customerDao.loadAllById(uid).amount
                transactionDao.insertAll(
                    TransactionEntity(
                        0,
                        uid,
                        type,
                        description,
                        amount,
                        0,
                        null,
                        time
                    )
                )
                if(db.isOpen) {
                    db.close()
                }

                var msgType: String;
                if (type == 0){
                    if (totalAmt <= 0){
                        msgType = Config.purchaseDue
                    }else {
                        msgType = Config.purchaseAdv
                    }
                }else{
                    if (totalAmt <= 0){
                        msgType = Config.paidDue
                    }else {
                        msgType = Config.paidAdv
                    }
                }

                val msg:String = String.format(msgType, name, Math.abs(amount), "Suman Manna", Math.abs(totalAmt) )
                Log.d(TAG, "trans: $msg")
                SMSManager.sendSMS(number, msg)
            }catch (e: Exception){
                Log.d(TAG, "addRecord: ${e.printStackTrace()}")
            }

        }
        job.join()
        runOnUiThread {
            mAmount.text = null
            mDescription.text = null
        }
        getCustomer()

    }
}