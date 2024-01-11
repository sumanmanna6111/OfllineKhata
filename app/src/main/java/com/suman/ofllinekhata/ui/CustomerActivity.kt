package com.suman.ofllinekhata.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.helper.PrefManager
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.adapter.CustomerAdapter
import com.suman.ofllinekhata.databinding.ActivityCustomerBinding
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.model.CustomerModel
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException


class CustomerActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "CustomerActivity"
    }

    private lateinit var binding: ActivityCustomerBinding
    private var customerList: ArrayList<CustomerModel> = ArrayList()
    var adapter: CustomerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        adapter = CustomerAdapter(customerList)
        binding.listCustomer.setHasFixedSize(true)
        binding.listCustomer.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.listCustomer.adapter = adapter
        /*if (System.currentTimeMillis() > 1685385000000L) {
            this.finishAffinity();
            System.exit(0);
            }*/
        /*binding.listCustomer.addOnScrollListener(object : OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d("TAG", "onScrolled: "+dy)
                if(dy > 0){
                    val first: Int = (binding.listCustomer.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (first > 0 && binding.cvDue.visibility == View.VISIBLE) {
                        binding.cvDue.visibility = View.GONE
                        binding.cvAdvance.visibility = View.GONE
                    }
                }else{
                    val first: Int = (binding.listCustomer.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (first == 2 && binding.cvDue.visibility == View.GONE) {
                        binding.cvDue.visibility = View.VISIBLE
                        binding.cvAdvance.visibility = View.VISIBLE
                    }
                }

            }
        })*/
        adapter!!.setOnClickListener(object : OnClickListener {
            override fun onClick(id: Int, name: String, number: String) {
                Intent(this@CustomerActivity, TransactionActivity::class.java).also {
                    it.putExtra("id", id)
                    it.putExtra("name", name)
                    it.putExtra("number", number)
                    startActivity(it)
                }
            }
        })

        binding.btnAddCustomer.setOnClickListener {
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }
        requestPermission()

        val prefManager = PrefManager(this)
        val lastBackUp: Long = prefManager.getLong("backup")
        val hour = System.currentTimeMillis() - lastBackUp
        if (hour > 14400000L) {
            backup()
        }
        checkPin()
    }

    private fun checkPin() {
        if (PrefManager(this).getBoolean("ispin")) {
            val intent = Intent(this, PinActivity::class.java)
            startForPinResult.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        customerList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            getCustomer()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 9) {
            var isAllGranted = true
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        this@CustomerActivity,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) isAllGranted = false
            }
            if (!isAllGranted) {
                //requestPermission
                Toast.makeText(this, "Need All Permission", Toast.LENGTH_SHORT).show()
            } else {
                // TODO -- Do any work when all permission are granted

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SEND_SMS
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_CONTACTS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }
        }
    }

    private fun requestPermission() {
        /* if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getCallDetails();
                //requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE},0);
            }
        }*/
        var permissions = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions  = arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS
            )
        }
        ActivityCompat.requestPermissions(this@CustomerActivity, permissions, 9)
        //requestPermissions(permissions, 9)

    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun getCustomer() {
        val job = CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDataBase(applicationContext)
            val customerDao = db.customerDao()
            val customers: List<CustomerEntity> = customerDao.getAll()
            val getCredit = customerDao.getTotalCredit()
            val getDebit = customerDao.getTotalDebit()
            if (db.isOpen) db.close()
            val creditBalance = "\u20B9${getCredit ?: 0.00f}"
            val debitBalance = "\u20B9${getDebit ?: 0.00f}"
            MainScope().launch(Dispatchers.Main) {
                binding.tvTotalCredit.text = creditBalance
                binding.tvTotalDebit.text = debitBalance
            }
            for (customer in customers) {
                customerList.add(
                    CustomerModel(
                        customer.id,
                        customer.name,
                        customer.number,
                        customer.amount
                    )
                )
            }
        }
        job.join()
        runOnUiThread { adapter?.notifyDataSetChanged() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_item, menu)
        val menuItem: MenuItem = menu!!.findItem(R.id.number_search)
        val searchView: SearchView = menuItem.actionView as SearchView
        /*val txtSearch = searchView.findViewById<View>(R.id.search_src_text) as EditText
        txtSearch.setHintTextColor(Color.LTGRAY)
        txtSearch.setTextColor(Color.WHITE)*/
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.queryHint = "Enter Name or Number"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter!!.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> startActivity(
                Intent(
                    this@CustomerActivity,
                    SettingsActivity::class.java
                )
            )

            R.id.backup -> {
                backup()
            }

            R.id.about -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backup() {
        try {
            val currentDBPath = getDatabasePath("khata.db").absolutePath
            val storage = Environment.getExternalStorageDirectory().absolutePath
            /*Log.d("TAG", "onOptionsItemSelected: $currentDBPath")
            Log.d("TAG", "onOptionsItemSelected: $storage")*/
            File(currentDBPath).copyTo(File("$storage/OfflineKhata/backup.db"), true)
            Toast.makeText(this, "Backup to OfflineKhata/backup.db", Toast.LENGTH_LONG).show()
            PrefManager(this).setLong("backup", System.currentTimeMillis())
        } catch (e: IOException) {
            Log.e(TAG, "backup: ", e)
            Toast.makeText(this, "failed to write external storage", Toast.LENGTH_LONG).show()
        }
    }


    private val startForPinResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    if (data.getStringExtra("pin") == PrefManager(this).getString("pin")) {
                        Log.d(TAG, "PIN: pin ok")
                    } else {
                        checkPin()
                        Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                checkPin()
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
            }
        }

}