package com.example.testapp.ui.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.testapp.domain.usecases.GetBlenderReports
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupervisorProfileScreenViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    fun getSignature(): String? {
        return sharedPreferences.getString("signature", null)
    }
}