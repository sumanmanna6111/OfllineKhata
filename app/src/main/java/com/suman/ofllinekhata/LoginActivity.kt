package com.suman.ofllinekhata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.suman.ofllinekhata.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prefManager = PrefManager(this)
        if (prefManager.getBoolean("islogin")){
            startActivity(Intent(this, CustomerActivity::class.java))
            finish()
        }
        binding.btnLogin.setOnClickListener { login(prefManager) }
    }

    private fun login(prefManager: PrefManager){
        if (binding.edShopName.text.toString().isEmpty()) {
            binding.edShopName.error = "Enter Shop Name"
            return
        }
        prefManager.setString("company", binding.edShopName.text.toString())
        prefManager.setBoolean("islogin", true)
        startActivity(Intent(this, CustomerActivity::class.java))
        finish()
    }
}