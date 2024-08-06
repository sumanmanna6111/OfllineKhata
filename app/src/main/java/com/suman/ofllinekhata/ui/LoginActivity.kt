package com.suman.ofllinekhata.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.helper.PrefManager
import com.suman.ofllinekhata.databinding.ActivityLoginBinding
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.viewmodel.LoginViewModel
import com.suman.ofllinekhata.viewmodelfactory.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        prefManager = PrefManager(application.applicationContext)
        if (prefManager.getBoolean("islogin")){
            startActivity(Intent(this, CustomerActivity::class.java))
            finish()
            return
        }
        val application = requireNotNull(this).application
        val factory = LoginViewModelFactory(application)
        val loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        binding.loginViewModel = loginViewModel

        setConfig()
        binding.btnLogin.setOnClickListener {
            login(loginViewModel.shopName.value.toString())
        }
    }


    private fun setConfig(){
        if (prefManager.getString("config").isNullOrEmpty()){
            prefManager.setBoolean("sms", true)
            prefManager.setString("config","yes")
        }
    }

    private fun login(shopName: String){
        if (binding.edShopName.text.toString().isEmpty()) {
            binding.edShopName.error = "Enter Shop Name"
            return
        }
        prefManager.setString("company", shopName)
        prefManager.setBoolean("islogin", true)
        startActivity(Intent(this, CustomerActivity::class.java))
        finish()

    }


}