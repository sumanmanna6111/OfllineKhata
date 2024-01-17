package com.suman.ofllinekhata.ui

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.suman.ofllinekhata.room.AppDatabase
import com.suman.ofllinekhata.helper.Config
import com.suman.ofllinekhata.helper.PrefManager
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.helper.SMSManager
import com.suman.ofllinekhata.databinding.ActivityAddCustomerBinding
import com.suman.ofllinekhata.repository.AddCustomerRepository
import com.suman.ofllinekhata.room.entity.CustomerEntity
import com.suman.ofllinekhata.room.entity.TransactionEntity
import com.suman.ofllinekhata.viewmodel.AddCustomerViewModel
import com.suman.ofllinekhata.viewmodelfactory.AddCustomerViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class AddCustomerActivity : AppCompatActivity() {
    companion object{
        const val TAG = "AddCustomerActivity"
    }
    private lateinit var binding: ActivityAddCustomerBinding
    private lateinit var addCustomerViewModel: AddCustomerViewModel
    private lateinit var prefManager: PrefManager
    private var type: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_customer)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        prefManager = PrefManager(this@AddCustomerActivity)
        val database = AppDatabase.getDataBase(applicationContext)
        val repository = AddCustomerRepository(database)
        addCustomerViewModel = ViewModelProvider(this, AddCustomerViewModelFactory(repository))[AddCustomerViewModel::class.java]

        binding.dueType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_credit -> {
                    type = 0
                }
                R.id.btn_bebit -> {
                    type = 1
                }
            }
        }
        binding.btnAddCustomer.setOnClickListener {
            addCustomer()
        }
        binding.ilCustName.setEndIconOnClickListener {
            val i = Intent(Intent.ACTION_PICK)
            i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(i, 300)
            //openContacts.launch(null)
        }
    }

    private fun addCustomer() {
        if (binding.edCustomerName.text.isEmpty()){
            binding.edCustomerName.error = "Please input name"
            return
        }else if(binding.edCustomerNumber.text.isEmpty()){
            binding.edCustomerNumber.error = "Please input number"
            return
        }else if(binding.edCustomerAmount.text.isEmpty()){
            binding.edCustomerAmount.error = "Please enter amount"
            return
        }
        val name: String = binding.edCustomerName.text.toString()
        val number: String = binding.edCustomerNumber.text.toString()
        var amount: Float = binding.edCustomerAmount.text.toString().toFloat()
        val description: String = binding.edCustomerDesc.text.toString()
        try {
            if (type == 0)amount = -amount
            addCustomerViewModel.addCustomer(name, number, amount, type, description)
            if (prefManager.getBoolean("sms")){
                val msgType: String = if (type == 0){
                    if (amount <= 0){
                        Config.purchaseDue
                    }else {
                        Config.purchaseAdv
                    }
                }else{
                    if (amount <= 0){
                        Config.paidDue
                    }else {
                        Config.paidAdv
                    }
                }
                val msg:String = String.format(msgType, name, abs(amount), prefManager.getString("company"), abs(amount))
                SMSManager.sendSMS(number, msg)
            }

            finish()
        }catch (e: Exception){
            Log.d(TAG, "addCustomer: ${e.printStackTrace()}")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    /*@SuppressLint("Range")
    private val openContacts = registerForActivityResult(ActivityResultContracts.PickContact()) {

        val contactData: Uri? = it
        val phone: Cursor? = contentResolver.query(contactData!!, null, null, null, null)
        if (phone!!.moveToFirst()) {
            val contactName: String = phone.getString(phone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

            // To get number - runtime permission is mandatory.
            val id: String = phone.getString(phone.getColumnIndex(ContactsContract.Contacts._ID))
            if (phone.getString(phone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                while (phones!!.moveToNext()) {
                    val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    Log.d("## Number", phoneNumber)
                }
                phones!!.close()
            }

            Log.d("## Contact Name", contactName)
        }

    }*/

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 300 && resultCode == RESULT_OK) {
            val contactUri = data?.data ?: return
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val cursor = this.contentResolver.query(
                contactUri, projection,
                null, null, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val name = cursor.getString(nameIndex)
                var number = cursor.getString(numberIndex)
                number = number.toString().replace("+91", "").replace(" ", "").replace("-", "")
                binding.edCustomerName.setText(name)
                binding.edCustomerNumber.setText(number)
            }
            cursor?.close()
        }
    }
}