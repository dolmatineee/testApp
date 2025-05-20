package com.example.testapp.ui.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.BaseReport
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.usecases.GetReports
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

    private val _reports = MutableStateFlow<List<BaseReport>?>(null)
    val reports: StateFlow<List<BaseReport>?> = _reports

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        val employeeId = sharedPreferences.getInt("employeeId", 0)
        loadReports(ReportFilters())
    }

    fun loadReports(filters: ReportFilters) {
        viewModelScope.launch {
            _isLoading.value = true
            _reports.value = getReports.invoke(filters)
            _isLoading.value = false
        }
    }
}