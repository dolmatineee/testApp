package com.example.testapp.utils

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun getFullName(): String? {
        return sharedPreferences.getString("fullName", null)
    }

    fun getPosition(): String? {
        return sharedPreferences.getString("position", null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }


}