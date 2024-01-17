package com.suman.ofllinekhata.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.helper.Config
import com.suman.ofllinekhata.helper.PrefManager
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.helper.SMSManager
import com.suman.ofllinekhata.databinding.ActivityDetailsBinding
import com.suman.ofllinekhata.repository.DetailsRepository
import com.suman.ofllinekhata.viewmodel.DetailsViewModel
import com.suman.ofllinekhata.viewmodelfactory.DetailsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import androidx.lifecycle.Observer

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var detailsViewModel: DetailsViewModel
    private lateinit var prefManager: PrefManager
    var id: Int = 0
    var amount: Float = 0f
    private var uid: Int = 0
    var msg: String = ""
    var name = ""
    var number = ""
    var balance = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val database = AppDatabase.getDataBase(applicationContext)
        val repository = DetailsRepository(database)
        detailsViewModel = ViewModelProvider(
            this,
            DetailsViewModelFactory(repository)
        )[DetailsViewModel::class.java]
        prefManager = PrefManager(this)
        id = intent.getIntExtra("id", 0)
        name = intent.getStringExtra("name")!!
        number = intent.getStringExtra("number")!!
        balance = intent.getFloatExtra("amount", 0f)
        binding.btnSaveTran.setOnClickListener {
            if (binding.tranDetailIsClear.isChecked && binding.tranDetailIsClear.isClickable) {
                saveTran()
            } else {
                Toast.makeText(this, "nothing change", Toast.LENGTH_SHORT).show()
            }
        }
        getTranDetails()
    }

    private fun saveTran() {
        detailsViewModel.tranUpdate(if (binding.tranDetailIsClear.isChecked) 1 else 0, id)
        detailsViewModel.customerUpdate(amount, uid)
        if (prefManager.getBoolean("sms")) {
            val msgType: String = if (balance <= 0) {
                Config.paidDue
            } else {
                Config.paidAdv
            }
            val msg: String = String.format(
                msgType,
                name,
                abs(amount),
                prefManager.getString("company"),
                abs(balance)
            )
            SMSManager.sendSMS(number, msg)
        }
        finish()
    }

    private fun getTranDetails() {
        detailsViewModel.getTransaction(id).observe(this, Observer {
            uid = it.uid
            amount = it.amount

            val totalAmt: Float = balance
            val msgType: String = if (it.type == 0) {
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
                name,
                abs(amount),
                prefManager.getString("company"),
                abs(totalAmt)
            )
            binding.tranDetailAmt.setText(abs(amount).toString())
            binding.tranDetailDesc.setText(it.description)
            if (it.type == 1) binding.btnBebit.isChecked =
                true else binding.btnCredit.isChecked = true
            binding.tranDetailIsClear.isChecked = it.received == 1
            binding.tranDetailIsClear.isClickable = it.received != 1
            binding.tranDetailClear.setText(
                it.clear?.let {
                    SimpleDateFormat(
                        "dd MMM yyyy hh:mm a",
                        Locale.ENGLISH
                    ).format(Date(it))
                })
            binding.tranDetailTime.setText(
                SimpleDateFormat(
                    "dd MMM yyyy hh:mm a",
                    Locale.ENGLISH
                ).format(Date(it.time))
            )
        })

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