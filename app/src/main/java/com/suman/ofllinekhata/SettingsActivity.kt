package com.suman.ofllinekhata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.suman.ofllinekhata.databinding.ActivitySettingsBinding

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

        binding.smsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            prefManager.setBoolean("sms", isChecked)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}