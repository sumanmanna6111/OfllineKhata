package com.suman.ofllinekhata

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.suman.ofllinekhata.databinding.ActivitySettingsBinding
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
        binding.smsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            prefManager.setBoolean("sms", isChecked)
        }

        binding.lockSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                val intent = Intent(this, PinActivity::class.java)
                startForPinResult.launch(intent)
            }else{
                prefManager.setBoolean("ispin", false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    private fun restore() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/octet-stream")
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
                        val currentDBPath = getDatabasePath("khata.db").absolutePath
                        //Log.d("TAG", "onOptionsItemSelected: $currentDBPath")
                        //Log.d("TAG", "onOptionsItemSelected: $path")
                        File(path).copyTo(
                            File(
                                currentDBPath
                            ), true
                        )
                        Toast.makeText(this, "backup imported successfully", Toast.LENGTH_LONG)
                            .show()
                    } catch (e: IOException) {
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