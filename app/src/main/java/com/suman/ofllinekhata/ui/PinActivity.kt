package com.suman.ofllinekhata.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.databinding.ActivityPinBinding

class PinActivity : AppCompatActivity() {
    private lateinit var pinBinding: ActivityPinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pinBinding = DataBindingUtil.setContentView(this, R.layout.activity_pin)
        textWatcher()
        clickInit()
    }

    private fun clickInit() {
        pinBinding.lbtn1.setOnClickListener {
            setPinText(
                pinBinding.lbtn1.text.toString())
        }
        pinBinding.lbtn2.setOnClickListener {
                setPinText(pinBinding.lbtn2.text.toString())
        }
        pinBinding.lbtn3.setOnClickListener {
                setPinText(pinBinding.lbtn3.text.toString())
        }
        pinBinding.lbtn4.setOnClickListener {
                setPinText(pinBinding.lbtn4.text.toString())
        }
        pinBinding.lbtn5.setOnClickListener {
                setPinText(pinBinding.lbtn5.text.toString())
        }
        pinBinding.lbtn6.setOnClickListener {
                setPinText(pinBinding.lbtn6.text.toString())
        }
        pinBinding.lbtn7.setOnClickListener {
                setPinText(pinBinding.lbtn7.text.toString())
        }
        pinBinding.lbtn8.setOnClickListener {
                setPinText(pinBinding.lbtn8.text.toString())
        }
        pinBinding.lbtn9.setOnClickListener {
                setPinText(pinBinding.lbtn9.text.toString())
        }
        pinBinding.lbtn0.setOnClickListener {
                setPinText(pinBinding.lbtn0.text.toString())
        }
        pinBinding.lbtnclear.setOnClickListener {
                clearPinText()
        }
        pinBinding.btnok.setOnClickListener {
                if (pinBinding.t4.text.toString().isNotEmpty()) {
                    finish()
                }
        }
    }

    private fun setPinText(pinText: String) {
        if (pinBinding.t1.text.toString().isEmpty()) {
            pinBinding.t1.setText(pinText)
            return
        }
        if (pinBinding.t2.text.toString().isEmpty()) {
            pinBinding.t2.setText(pinText)
            return
        }
        if (pinBinding.t3.text.toString().isEmpty()) {
            pinBinding.t3.setText(pinText)
            return
        }
        if (pinBinding.t4.text.toString().isEmpty()) {
            pinBinding.t4.setText(pinText)
        }
    }

    private fun clearPinText() {
        if (pinBinding.t4.text.toString().isNotEmpty()) {
            pinBinding.t4.setText("")
            return
        }
        if (pinBinding.t3.text.toString().isNotEmpty()) {
            pinBinding.t3.setText("")
            return
        }
        if (pinBinding.t2.text.toString().isNotEmpty()) {
            pinBinding.t2.setText("")
            return
        }
        if (pinBinding.t1.text.toString().isNotEmpty()) {
            pinBinding.t1.setText("")
        }
    }

    private fun textWatcher() {
        pinBinding.t1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (pinBinding.t1.text.toString().length > 1) {
                    pinBinding.t1.setText("")
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (pinBinding.t1.text.toString().length == 1) {
                    pinBinding.t2.requestFocus()
                }
            }
        })
        pinBinding.t2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (pinBinding.t2.text.toString().length > 1) {
                    pinBinding.t2.setText("")
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (pinBinding.t2.text.toString().length == 1) {
                    pinBinding.t3.requestFocus()
                }
            }
        })
        pinBinding.t3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (pinBinding.t3.text.toString().length > 1) {
                    pinBinding.t3.setText("")
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (pinBinding.t3.text.toString().length == 1) {
                    pinBinding.t4.requestFocus()
                }
            }
        })
        pinBinding.t4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (pinBinding.t4.text.toString().length > 1) {
                    pinBinding.t4.setText("")
                }
            }

            override fun afterTextChanged(editable: Editable) {
                if (pinBinding.t4.text.toString().length == 1) {
                    val stringBuilder = StringBuilder()
                    stringBuilder.append(pinBinding.t1.text.toString())
                    stringBuilder.append(pinBinding.t2.text.toString())
                    stringBuilder.append(pinBinding.t3.text.toString())
                    stringBuilder.append(pinBinding.t4.text.toString())
                    val intent = Intent()
                    intent.putExtra("pin", stringBuilder.toString())
                    setResult(RESULT_OK, intent)
                    /*Toast.makeText(PinActivity.this, stringBuilder, Toast.LENGTH_SHORT).show();*/
                }
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_CANCELED, null)
        finish()
        super.onBackPressed()
    }
}