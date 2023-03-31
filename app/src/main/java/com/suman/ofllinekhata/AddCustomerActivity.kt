package com.suman.ofllinekhata

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.suman.ofllinekhata.databinding.ActivityAddCustomerBinding
import com.suman.ofllinekhata.entity.CustomerEntity
import com.suman.ofllinekhata.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCustomerActivity : AppCompatActivity() {
    private val TAG = "AddCustomerActivity"
    private lateinit var binding: ActivityAddCustomerBinding
    var type: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.dueType.setOnCheckedChangeListener { group, checkedId ->
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
            binding.edCustomerName.setError("Please input name")
            return
        }else if(binding.edCustomerNumber.text.isEmpty()){
            binding.edCustomerNumber.setError("Please input number")
            return
        }else if(binding.edCustomerAmount.text.isEmpty()){
            binding.edCustomerAmount.setError("Please enter amount")
            return
        }
        val name: String = binding.edCustomerName.text.toString()
        val number: String = binding.edCustomerNumber.text.toString()
        var amount: Float = binding.edCustomerAmount.text.toString().toFloat()
        val description: String = binding.edCustomerDesc.text.toString()
        val time: Long = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "khata.db"
            ).build()
            val customerDao = db.customerDao()
            val transactionDao = db.transactionDao()
            if (type == 0)amount = -amount
            try {
                customerDao.insertAll(CustomerEntity(0, name, number, amount, time))
                val uid = customerDao.getLastUser().get(0).id
                transactionDao.insertAll(
                    TransactionEntity(
                        0,
                        uid,
                        type,
                        description,
                        amount,
                        0,
                        null,
                        time
                    )
                )
                if(db.isOpen) {
                    db.close()
                }
                var msgType: String = "";
                if (type == 0){
                    if (amount <= 0){
                        msgType = Config.purchaseDue
                    }else {
                        msgType = Config.purchaseAdv
                    }
                }else{
                    if (amount <= 0){
                        msgType = Config.paidDue
                    }else {
                        msgType = Config.paidAdv
                    }
                }
                val msg:String = String.format(msgType, name, Math.abs(amount), "Suman Manna", Math.abs(amount))

                SMSManager.sendSMS(number, msg)
                finish()
            }catch (e: Exception){
                Log.d(TAG, "addCustomer: ${e.printStackTrace()}")
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
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
                var name = cursor.getString(nameIndex)
                var number = cursor.getString(numberIndex)
                number = number.toString().replace("+91", "").replace(" ", "").replace("-", "")
                binding.edCustomerName.setText(name)
                binding.edCustomerNumber.setText(number)
            }
            cursor?.close()
        }
    }
}