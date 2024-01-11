package com.suman.ofllinekhata.helper

import android.telephony.SmsManager

object SMSManager {
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

/* public class SMSManager {
    public static boolean sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
*/