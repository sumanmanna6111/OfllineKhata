package com.suman.ofllinekhata.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.helper.Config
import com.suman.ofllinekhata.helper.PrefManager
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.helper.SMSManager
import com.suman.ofllinekhata.adapter.TransactionAdapter
import com.suman.ofllinekhata.databinding.ActivityTransactionBinding
import com.suman.ofllinekhata.databinding.BottomSheetDialogBinding
import com.suman.ofllinekhata.room.entity.TransactionEntity
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.model.TransactionModel
import com.suman.ofllinekhata.repository.TransactionRepository
import com.suman.ofllinekhata.viewmodel.TransactionViewModel
import com.suman.ofllinekhata.viewmodelfactory.TransactionViewModelFactory
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.math.abs

class TransactionActivity : AppCompatActivity() {
    private val TAG = "TransactionActivity"
    private lateinit var binding: ActivityTransactionBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private var tranList: ArrayList<TransactionEntity> = ArrayList()
    private lateinit var adapter: TransactionAdapter
    private var uid: Int = 0
    var name: String = ""
    var number: String = ""
    private var type: Int = 0
    private var balance = 0f
    private lateinit var prefManager: PrefManager
    private lateinit var bottomSheet: BottomSheetDialogBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val  database = AppDatabase.getDataBase(applicationContext)
        val repository = TransactionRepository(database)
        transactionViewModel = ViewModelProvider(this, TransactionViewModelFactory(repository))[TransactionViewModel::class.java]

        adapter = TransactionAdapter(tranList)
        prefManager = PrefManager(this@TransactionActivity)
        binding.listTransaction.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.listTransaction.adapter = adapter
        uid = intent.getIntExtra("id", 0)
        name = intent.getStringExtra("name") ?: ""
        number = intent.getStringExtra("number") ?: ""
        binding.toolbar.title = name
        val dialog = BottomSheetDialog(this, R.style.DialogStyle)
        //dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        bottomSheet = BottomSheetDialogBinding.inflate(layoutInflater)

        bottomSheet.icClose.setOnClickListener {
            dialog.dismiss()
        }
        binding.cvDue.setOnClickListener {
            val msg: String = String.format(
                Config.totalDue,
                name,
                binding.tvTotalBal.text,
                prefManager.getString("company")
            )
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        binding.cvAdvance.setOnClickListener {
            val msg: String = String.format(
                Config.totalAdvance,
                name,
                binding.tvTotalDebit.text,
                prefManager.getString("company")
            )
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        bottomSheet.dueType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_credit -> {
                    type = 0
                }

                R.id.btn_bebit -> {
                    type = 1
                }
            }
        }
        bottomSheet.btnAddTran.setOnClickListener {
            if (bottomSheet.edCustomerAmount.text.isEmpty()) {
                bottomSheet.edCustomerAmount.error = "Enter Amount"
            } else {
                addRecord()
                dialog.dismiss()
            }

        }
        dialog.setCancelable(true)
        dialog.setContentView(bottomSheet.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        adapter.setOnClickListener(object : OnClickListener {
            override fun onClick(id: Int, name: String, number: String) {
                startActivity(
                    Intent(
                        this@TransactionActivity,
                        DetailsActivity::class.java
                    ).also {
                        it.putExtra("id", id)
                        it.putExtra("name", name)
                        it.putExtra("number", number)
                        it.putExtra("amount", balance)
                    }
                )
            }
        })

        binding.btnAddTran.setOnClickListener {
            dialog.show()
        }
        getCustomer()
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun getCustomer() {
        transactionViewModel.getTransaction(uid).observe(this, Observer {
            adapter.updateList(it)
        })
        transactionViewModel.getTotal(uid).observe(this, Observer {
            balance = it ?: 0.00f
            if (balance < 0) {
                binding.credit = "\u20B9${balance}"
                binding.debit = "₹0.0"
            }else{
                binding.credit = "₹0.0"
                binding.debit = "\u20B9${balance}"
            }
        })


    }

    private fun addRecord() {
        var amount: Float = bottomSheet.edCustomerAmount.text.toString().toFloat()
        val description: String = bottomSheet.edCustomerDesc.text.toString()
        val time: Long = System.currentTimeMillis()

            //val db = AppDatabase.getDataBase(applicationContext)
            //val customerDao = db.customerDao()
            //val transactionDao = db.transactionDao()
            if (type == 0) amount = -amount
            try {
                transactionViewModel.addTransaction(uid, type, description, amount)
                transactionViewModel.updateBalance(amount, uid)
                val totalAmt: Float = amount
                if (prefManager.getBoolean("sms")) {
                    val msgType: String = if (type == 0) {
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

                    val msg: String = String.format(
                        msgType,
                        name,
                        abs(amount),
                        prefManager.getString("company"),
                        abs(totalAmt)
                    )
                    SMSManager.sendSMS(number, msg)
                }

            } catch (e: Exception) {
                Log.d(TAG, "addRecord: ${e.printStackTrace()}")
            }

        bottomSheet.edCustomerAmount.text = null
        bottomSheet.edCustomerDesc.text = null

    }
}