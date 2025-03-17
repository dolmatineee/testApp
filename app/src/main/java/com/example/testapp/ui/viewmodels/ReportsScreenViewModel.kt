package com.example.testapp.ui.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.Report
import com.example.testapp.domain.usecases.GetReports
import com.example.testapp.domain.usecases.LoginEmployee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsScreenViewModel @Inject constructor(
    private val getReports: GetReports,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _reports = MutableStateFlow<List<Report>?>(null)
    val reports: StateFlow<List<Report>?> = _reports



    init {
        val employeeId = sharedPreferences.getInt("employeeId", 0)
        loadReports(employeeId)
    }

    fun loadReports(employeeId: Int) {
        viewModelScope.launch {
            _reports.value = getReports.invoke(employeeId)
        }
    }
}