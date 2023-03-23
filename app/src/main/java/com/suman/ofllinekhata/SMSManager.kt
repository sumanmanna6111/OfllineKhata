package com.suman.ofllinekhata

import android.telephony.SmsManager

class SMSManager {
    fun sendSMS(phoneNo: String?, msg: String?): Boolean {
        return try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }
}