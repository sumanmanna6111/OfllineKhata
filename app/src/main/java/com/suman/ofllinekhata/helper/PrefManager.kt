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

    fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun setString(key: String, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    fun setInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreference.getBoolean(key, false)
    }

    fun setLong(key: String, value: Long) {
        editor.putLong(key, value)
        editor.commit()
    }

    fun getLong(key: String): Long {
        return sharedPreference.getLong(key, 0)
    }

    fun getString(key: String): String? {
        return if (sharedPreference.contains(key)) {
            sharedPreference.getString(key, null)
        } else ""
    }

    fun remove(key: String) {
        if (sharedPreference.contains(key)) {
            editor.remove(key)
            editor.commit()
        }
    }

    fun getInt(key: String): Int {
        return sharedPreference.getInt(key, 0)
    }

    fun clearAll() {
        editor.clear()
        editor.commit()
    }
}