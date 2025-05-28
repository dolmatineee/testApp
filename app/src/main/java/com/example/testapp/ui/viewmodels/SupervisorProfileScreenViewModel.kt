package com.example.testapp.ui.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.usecases.GetBlenderReports
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SupervisorProfileScreenViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    fun getSignature(): String? {
        return sharedPreferences.getString("signature", null)
    }

    fun clearUserData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferences.edit().clear().commit()
            }
        }

    }
}