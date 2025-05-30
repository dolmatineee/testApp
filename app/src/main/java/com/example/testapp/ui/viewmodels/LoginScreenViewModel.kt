package com.example.testapp.ui.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.usecases.LoginEmployee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginEmployee: LoginEmployee,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun login(phoneNumber: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val employee = loginEmployee(phoneNumber, password)
            if (employee != null) {
                sharedPreferences.edit {
                    putString("fullName", employee.fullName)
                    putString("position", employee.position)
                    putInt("employeeId", employee.id)
                    putBoolean("isLoggedIn", true)
                }
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}