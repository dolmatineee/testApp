package com.example.testapp.ui.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.BaseReport
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.models.ReportStatus
import com.example.testapp.domain.usecases.GetReports
import com.example.testapp.domain.usecases.GetStatuses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsScreenViewModel @Inject constructor(
    private val getReports: GetReports,
    private val getStatuses: GetStatuses,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _reports = MutableStateFlow<List<BaseReport>>(emptyList())
    val reports: StateFlow<List<BaseReport>> = _reports

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _statuses = MutableStateFlow<Map<Int, ReportStatus>>(emptyMap())
    val statuses: StateFlow<Map<Int, ReportStatus>> = _statuses

    init {
        val employeeId = sharedPreferences.getInt("employeeId", 0)
        loadReports(ReportFilters())
    }

    fun loadReports(filters: ReportFilters) {
        viewModelScope.launch {
            _isLoading.value = true
            _reports.value = getReports.invoke(filters)
            Log.e("FGHFGHFGHF", _reports.value.toString())
            _isLoading.value = false
        }
    }
    fun getStatusById(id: Int): Flow<ReportStatus?> {
        return flow {
            try {
                val status = getStatuses(id)
                Log.e("ReportViewModel", status.toString())
                emit(status)

            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error getting photos", e)
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
    }

}