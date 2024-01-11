package com.suman.ofllinekhata.helper

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private var sharedPreference: SharedPreferences
    private var editor: SharedPreferences.Editor
    private val name = "share_preference"

    init {
        sharedPreference = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
    }

    fun setBoolean(PREF_NAME: String?, VALUE: Boolean?) {
        editor.putBoolean(PREF_NAME, VALUE!!)
        editor.commit()
    }

    fun setString(PREF_NAME: String?, VALUE: String?) {
        editor.putString(PREF_NAME, VALUE)
        editor.commit()
    }

    fun setInt(PREF_NAME: String?, VALUE: Int) {
        editor.putInt(PREF_NAME, VALUE)
        editor.commit()
    }

    fun getBoolean(PREF_NAME: String?): Boolean {
        return sharedPreference.getBoolean(PREF_NAME, false)
    }

    fun setLong(PREF_NAME: String?, VALUE: Long) {
        editor.putLong(PREF_NAME, VALUE)
        editor.commit()
    }

    fun getLong(key: String?): Long {
        return sharedPreference.getLong(key, 0)
    }

    fun getString(PREF_NAME: String?): String? {
        return if (sharedPreference.contains(PREF_NAME)) {
            sharedPreference.getString(PREF_NAME, null)
        } else ""
    }

    fun remove(PREF_NAME: String?) {
        if (sharedPreference.contains(PREF_NAME)) {
            editor.remove(PREF_NAME)
            editor.commit()
        }
    }

    fun getInt(key: String?): Int {
        return sharedPreference.getInt(key, 0)
    }

    fun clearAll() {
        editor.clear()
        editor.commit()
    }
}