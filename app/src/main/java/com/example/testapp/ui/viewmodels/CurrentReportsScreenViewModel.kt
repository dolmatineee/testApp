package com.example.testapp.ui.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.BaseReport
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.ReportPhoto
import com.example.testapp.domain.models.ReportType
import com.example.testapp.domain.models.ReportTypeEnum
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.usecases.GetBlenderPhotos
import com.example.testapp.domain.usecases.GetBlenderReports
import com.example.testapp.domain.usecases.GetCustomers
import com.example.testapp.domain.usecases.GetFields
import com.example.testapp.domain.usecases.GetLayers
import com.example.testapp.domain.usecases.GetSupervisorReports
import com.example.testapp.domain.usecases.GetWells
import com.example.testapp.domain.usecases.UploadSupervisorSignatureBlenderReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SupervisorReportsViewModel @Inject constructor(
    private val getBlenderReports: GetBlenderReports,
    private val getBlenderPhotos: GetBlenderPhotos,
    private val getSupervisorReports: GetSupervisorReports,
    private val getFields: GetFields,
    private val getWells: GetWells,
    private val getLayers: GetLayers,
    private val getCustomers: GetCustomers,
    private val uploadSupervisorSignatureBlenderReport: UploadSupervisorSignatureBlenderReport,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {



    // Текущий отчет
    private val _currentReport = MutableStateFlow<BlenderReport?>(null)
    val currentReport: StateFlow<BlenderReport?> = _currentReport

    // Фото отчетов
    private val _reportPhotos = MutableStateFlow<List<ReportPhoto>>(emptyList())
    val reportPhotos: StateFlow<List<ReportPhoto>> = _reportPhotos

    private val _reports = MutableStateFlow<List<BaseReport>>(emptyList())
    val reports: StateFlow<List<BaseReport>> = _reports

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            _reports.value = getSupervisorReports()
            _isLoading.value = false
        }
    }

    fun getReportPhotos(reportId: Int, reportType: ReportTypeEnum): Flow<List<ReportPhoto>> {
        return flow {
            try {
                val photos = getBlenderPhotos(
                    reportId,
                    reportType = reportType
                )
                emit(photos)
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error getting photos", e)
                emit(emptyList())
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getSignature(): String? {
        return sharedPreferences.getString("signature", null)
    }

    fun uploadSupervisorSignature(
        reportId: Int,
        reportType: ReportTypeEnum,
        signatureFile: File,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val urlSupervisorSignature = uploadSupervisorSignatureBlenderReport(reportId, reportType, signatureFile)
            if (urlSupervisorSignature != null) {
                onResult(true)
            } else {
                onResult(false)
            }
        }


    }


    // Очистка текущего отчета
    fun clearCurrentReport() {
        _currentReport.value = null
        _reportPhotos.value = emptyList()
    }

    fun getFieldById(id: Int): Flow<Field?> {
        return flow {
            try {
                val field = getFields(id)
                emit(field)
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error getting photos", e)
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
    }


    fun getWellById(id: Int): Flow<Well?> {
        return flow {
            try {
                val well = getWells(id)
                emit(well)
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error getting photos", e)
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getLayerById(id: Int): Flow<Layer?> {
        return flow {
            try {
                val layer = getLayers(id)
                emit(layer)
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error getting photos", e)
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getCustomerById(id: Int): Flow<Customer?> {
        return flow {
            try {
                val customer = getCustomers(id)
                emit(customer)
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error getting photos", e)
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
    }


}