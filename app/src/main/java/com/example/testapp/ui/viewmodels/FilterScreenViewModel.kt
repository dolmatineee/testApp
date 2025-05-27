package com.example.testapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.models.ReportType
import com.example.testapp.domain.models.ReportTypeEnum
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.usecases.GetReportTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FilterScreenViewModel @Inject constructor(
    private val getReportTypes: GetReportTypes
) : ViewModel() {
    private val _reportTypes = MutableStateFlow<List<ReportType>>(emptyList())
    val reportTypes: StateFlow<List<ReportType>> = _reportTypes.asStateFlow()

    private val _selectedReportTypeId = MutableStateFlow<Int?>(null)
    val selectedReportTypeId: StateFlow<Int?> = _selectedReportTypeId.asStateFlow()

    private val _selectedFields = MutableStateFlow<List<Field>>(emptyList())
    private val _selectedWells = MutableStateFlow<List<Well>>(emptyList())
    private val _selectedLayers = MutableStateFlow<List<Layer>>(emptyList())
    private val _selectedCustomers = MutableStateFlow<List<Customer>>(emptyList())
    private val _selectedDateRange = MutableStateFlow<ClosedRange<LocalDate>?>(null)
    val selectedDateRange: StateFlow<ClosedRange<LocalDate>?> = _selectedDateRange.asStateFlow()

    init {
        loadData()

    }

    private fun loadData() {
        viewModelScope.launch {
            _reportTypes.value = getReportTypes()
        }
    }

    fun onReportTypeSelected(typeId: Int?) {
        _selectedReportTypeId.value = typeId
        Log.e("onReportTypeSelected", typeId.toString())
    }

    fun setFields(fields: List<Field>) {
        _selectedFields.value = fields
    }

    fun setWells(wells: List<Well>) {
        _selectedWells.value = wells
    }

    fun setLayers(layers: List<Layer>) {
        _selectedLayers.value = layers
    }

    fun setCustomers(customers: List<Customer>) {
        _selectedCustomers.value = customers
    }

    fun setDateRange(dateRange: ClosedRange<LocalDate>?) {
        _selectedDateRange.value = dateRange
    }

    fun resetFilters() {
        _selectedReportTypeId.value = null
        _selectedFields.value = emptyList()
        _selectedWells.value = emptyList()
        _selectedLayers.value = emptyList()
        _selectedCustomers.value = emptyList()
        _selectedDateRange.value = null
    }

    fun getCurrentFilters(): ReportFilters {
        val reportType = _selectedReportTypeId.value?.let { id ->
            when (id) {
                ReportTypeEnum.BLENDER.id -> ReportTypeEnum.BLENDER
                ReportTypeEnum.ACID.id -> ReportTypeEnum.ACID
                ReportTypeEnum.GEL.id -> ReportTypeEnum.GEL
                else -> null
            }
        }
        Log.e("fghfghfgh", reportType.toString())

        return ReportFilters(
            reportType = reportType,
            fields = _selectedFields.value,
            wells = _selectedWells.value,
            layers = _selectedLayers.value,
            customers = _selectedCustomers.value,
            dateRange = _selectedDateRange.value
        )


    }
}