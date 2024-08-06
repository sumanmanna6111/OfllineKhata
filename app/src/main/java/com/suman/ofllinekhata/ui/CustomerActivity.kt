package com.suman.ofllinekhata.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.adapter.CustomerAdapter
import com.suman.ofllinekhata.databinding.ActivityCustomerBinding
import com.suman.ofllinekhata.helper.PrefManager
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.repository.CustomerRepository
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.viewmodel.CustomerViewModel
import com.suman.ofllinekhata.viewmodelfactory.CustomerViewModelFactory
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class CustomerActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "CustomerActivity"
    }

    private lateinit var binding: ActivityCustomerBinding
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var prefManager: PrefManager
    private var customerList: ArrayList<CustomerEntity> = ArrayList()
    private lateinit var adapter: CustomerAdapter
    private var totalCustomer = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer)
        setSupportActionBar(binding.toolbar)
        prefManager = PrefManager(this)
        val dao = AppDatabase.getDataBase(applicationContext).customerDao()
        val repository = CustomerRepository(dao)
        customerViewModel = ViewModelProvider(
            this,
            CustomerViewModelFactory(repository)
        )[CustomerViewModel::class.java]

        adapter = CustomerAdapter(customerList)
        binding.listCustomer.setHasFixedSize(true)
        binding.listCustomer.addItemDecoration(
            DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL
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
        adapter.setOnClickListener(object : OnClickListener {
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
        getCustomer()
        checkPin()
    }

    private fun checkPin() {
        if (PrefManager(this).getBoolean("ispin")) {
            val intent = Intent(this, PinActivity::class.java)
            startForPinResult.launch(intent)
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
            Manifest.permission.SEND_SMS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = arrayOf(
                Manifest.permission.SEND_SMS
            )
        }
        ActivityCompat.requestPermissions(this@CustomerActivity, permissions, 9)
        //requestPermissions(permissions, 9)

    }

    private fun getCustomer() {
        customerViewModel.getTotalCredit().observe(this, Observer {
            binding.due = "\u20B9 ${it ?: 0.00f}"
        })

        customerViewModel.getTotalDebit().observe(this, Observer {
            binding.advance = "\u20B9 ${it ?: 0.00f}"
        })

        customerViewModel.getCustomer().observe(this, Observer {
            adapter.updateAdapter(it)
            totalCustomer = it.size
            /*val lastBackUp: Long = prefManager.getLong("backup")
            val hour = System.currentTimeMillis() - lastBackUp
            if (hour > 14400000L) {
                backup()
            }*/
        })
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
                adapter.filter.filter(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.backup -> {
                Toast.makeText(this, "Features comming soon", Toast.LENGTH_SHORT).show()
                //scopedBackup()
            }
            R.id.setting -> {
                startActivity(
                    Intent(
                        this@CustomerActivity,
                        SettingsActivity::class.java
                    )
                )
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    /*private fun shareBackup() {
        try {
            if (totalCustomer > 0) {
                val storage = Environment.getExternalStorageDirectory().absolutePath
                if (File("$storage/OfflineKhata/backup.db").exists()){
                    val uri = Uri.parse("$storage/OfflineKhata/backup.db")
                    val sendIntent = Intent(Intent.ACTION_SEND)
                    sendIntent.type = "application/octet-stream"
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    sendIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(Intent.createChooser(sendIntent , "Share File"))
                }

            }else{
                Toast.makeText(this, "You have no transaction", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }*/
    private fun scopedBackup(){
        if (totalCustomer > 0) {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "application/octet-stream"
            intent.putExtra(Intent.EXTRA_TITLE, "khata.db")
            resultBackup.launch(intent)
        }else{
            Toast.makeText(this, "Empty Customer list", Toast.LENGTH_SHORT).show()
        }
    }
    val resultBackup = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            it.data?.data?.let {uri : Uri ->
                saveFile(uri)
            }
        }
    }

    private fun saveFile(uri: Uri){
            try {
                val currentDBPath = getDatabasePath("khata.db")
                Log.d(TAG, "saveFile: "+currentDBPath.length())
                val parcelFileDescriptor = this.contentResolver.openFileDescriptor(uri,"w")
                val fileOutputStream = FileOutputStream(parcelFileDescriptor?.fileDescriptor)
                FileInputStream(currentDBPath).use {input ->
                    fileOutputStream.use {output ->
                        input.copyTo(output)
                    }

                }
                val buffersize = 8 * 1024
               // copyFile(input, fileOutputStream, buffersize)
                fileOutputStream.close()
                parcelFileDescriptor?.close()
                Toast.makeText(this, "Backup completed", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "saveFile: success")
                Log.d(TAG, "saveFile: "+currentDBPath.length())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
    }
    /*private fun backup() {
        try {
            if (totalCustomer > 0) {
                val currentDBPath = getDatabasePath("khata.db").absolutePath
                val storage = Environment.getExternalStorageDirectory().absolutePath
                Log.d("TAG", "onOptionsItemSelected: $currentDBPath")
                Log.d("TAG", "onOptionsItemSelected: $storage")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()){
                        Log.d("TAG", "checkPermission: true")
                    }else{
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            .setData(Uri.parse("package:$packageName"))
                        startActivity(intent)
                        return
                    }

                }
                File(currentDBPath).copyTo(File("$storage/OfflineKhata/backup.db"), true)
                Toast.makeText(this, "Backup to OfflineKhata/backup.db", Toast.LENGTH_LONG)
                    .show()
                PrefManager(this).setLong("backup", System.currentTimeMillis())
            }else{
                Toast.makeText(this, "You have no transaction", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Log.e(TAG, "backup: ", e)
            Toast.makeText(this, "failed to write external storage", Toast.LENGTH_LONG).show()
        }
    }*/


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