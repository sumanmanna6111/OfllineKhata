package com.suman.ofllinekhata.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.suman.ofllinekhata.helper.PathUtils
import com.suman.ofllinekhata.helper.PrefManager
import com.suman.ofllinekhata.databinding.ActivitySettingsBinding
import com.suman.ofllinekhata.room.AppDatabase
import java.io.File
import java.io.IOException

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val prefManager = PrefManager(this)
        binding.smsSwitch.isChecked = prefManager.getBoolean("sms")
        binding.lockSwitch.isChecked = prefManager.getBoolean("ispin")

        binding.restore.setOnClickListener { restore() }
        binding.smsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefManager.setBoolean("sms", isChecked)
        }

        binding.lockSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                val intent = Intent(this, PinActivity::class.java)
                startForPinResult.launch(intent)
            }else{
                prefManager.setBoolean("ispin", false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, CustomerActivity::class.java))
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CustomerActivity::class.java))
        finish()
        super.onBackPressed()
    }

    private fun restore() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/octet-stream"
        startForResult.launch(intent)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val path: String = PathUtils.getPath(this, data.data)
                val file = File(path)
                if (file.extension == "db" && file.length() > 0) {
                    try {
                        val db = AppDatabase.getDataBase(applicationContext)
                        if (db.isOpen) db.close()
                        val currentDBPath = getDatabasePath("khata.db").absolutePath
                        Log.d("TAG", "onOptionsItemSelected: $currentDBPath")
                        Log.d("TAG", "onOptionsItemSelected: $path")
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            File(path).copyTo(
                                File(
                                    currentDBPath
                                ), true
                            )
                            Toast.makeText(this, "backup imported successfully", Toast.LENGTH_LONG)
                                .show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "something went wrong", Toast.LENGTH_LONG).show()
                    }
                }
            }


        }
    }
    private val startForPinResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                PrefManager(this).setString("pin", data.getStringExtra("pin"))
                PrefManager(this).setBoolean("ispin", true)
            }
        }
    }
}