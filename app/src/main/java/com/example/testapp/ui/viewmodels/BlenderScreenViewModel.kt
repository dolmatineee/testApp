package com.example.testapp.ui.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Photo
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.TestAttempt
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.usecases.GetCustomers
import com.example.testapp.domain.usecases.GetFields
import com.example.testapp.domain.usecases.GetLayers
import com.example.testapp.domain.usecases.GetWells
import com.example.testapp.domain.usecases.InsertBlenderReport
import com.example.testapp.domain.usecases.InsertPhotoReport

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BlenderScreenViewModel @Inject constructor(
    private val getFields: GetFields,
    private val getWells: GetWells,
    private val getLayers: GetLayers,
    private val getCustomers: GetCustomers,
    private val insertBlenderReport: InsertBlenderReport,
    private val updateBlenderReport: InsertBlenderReport,
    private val getReagentIdByName: InsertBlenderReport,
    private val insertPhotoReport: InsertPhotoReport,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _fields = MutableStateFlow<List<Field>>(emptyList())
    val fields: StateFlow<List<Field>> = _fields

    private val _wells = MutableStateFlow<List<Well>>(emptyList())
    val wells: StateFlow<List<Well>> = _wells

    private val _layers = MutableStateFlow<List<Layer>>(emptyList())
    val layers: StateFlow<List<Layer>> = _layers

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers

    private val _selectedField = MutableStateFlow<Field?>(null)
    val selectedField: StateFlow<Field?> = _selectedField

    private val _selectedWell = MutableStateFlow<Well?>(null)
    val selectedWell: StateFlow<Well?> = _selectedWell

    private val _selectedLayer = MutableStateFlow<Layer?>(null)
    val selectedLayer: StateFlow<Layer?> = _selectedLayer

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer

    private val _fieldError = MutableStateFlow(false)
    val fieldError: StateFlow<Boolean> = _fieldError

    private val _wellError = MutableStateFlow(false)
    val wellError: StateFlow<Boolean> = _wellError

    private val _employeeId = MutableStateFlow<Int?>(null)
    val employeeId: StateFlow<Int?> = _employeeId

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _reagents = listOf("ТТ ВС марка 1", "ТТ АВ", "ТТ ВА марка АР")
    val reagents: List<String> get() = _reagents

    private val _selectedReagentForTable = MutableStateFlow(_reagents.first())
    val selectedReagentForTable: StateFlow<String> get() = _selectedReagentForTable

    private val _testAttempts = MutableStateFlow<Map<String, List<TestAttempt>>>(emptyMap())
    val testAttempts: StateFlow<Map<String, List<TestAttempt>>> get() = _testAttempts

    private val _selectedAttemptId = MutableStateFlow<Int?>(null)
    val selectedAttemptId: StateFlow<Int?> get() = _selectedAttemptId

    private val _selectedAttempt = MutableStateFlow<TestAttempt?>(null)
    val selectedAttempt: StateFlow<TestAttempt?> get() = _selectedAttempt


    private val _photosForReagents = MutableStateFlow<Map<String, List<Photo>>>(emptyMap())
    val photosForReagents: StateFlow<Map<String, List<Photo>>> get() = _photosForReagents


    fun resetSuccessState() {
        _isSuccess.value = false
    }

    suspend fun saveReportAndGetId(report: BlenderReport, blenderReportCode: String): Int? {
        return withContext(Dispatchers.IO) {
            try {
                _isLoading.value = true
                insertBlenderReport.invoke(report, blenderReportCode)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun updateReportWithReagents(reportId: Int, reagents: List<Reagent>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = updateBlenderReport.invoke(reportId, reagents)
                result
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun getReagentIdByName(reagentName: String): Int? {
        return withContext(Dispatchers.IO) {
            try {
                getReagentIdByName.invoke(reagentName)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Создание нового теста
    private fun createNewTestAttempt(reagent: String): TestAttempt {
        val currentAttempts = _testAttempts.value[reagent] ?: emptyList()
        return TestAttempt(
            id = (currentAttempts.maxOfOrNull { it.id } ?: 0) + 1,
            reagentId = 0,
            flowRate = 0.0,
            concentration = 0.0,
            testTime = 0.0,
            actualAmount = 0.0
        )
    }



    fun selectReagent(reagent: String) {
        _selectedReagentForTable.value = reagent
        _selectedAttemptId.value = null
        _selectedAttempt.value = null
    }



    // Добавление нового теста
    fun addTestAttempt(reagent: String) {
        val newAttempt = createNewTestAttempt(reagent)
        _testAttempts.update { currentMap ->
            currentMap + (reagent to ((currentMap[reagent] ?: emptyList()) + newAttempt))
        }
        _selectedAttemptId.value = newAttempt.id
        _selectedAttempt.value = newAttempt
    }

    fun updateTestAttempt(reagent: String, updatedAttempt: TestAttempt) {
        val currentAttempts = _testAttempts.value[reagent] ?: emptyList()
        val updatedAttempts = currentAttempts.map {
            if (it.id == updatedAttempt.id) updatedAttempt else it
        }
        _testAttempts.update { currentMap ->
            currentMap + (reagent to updatedAttempts)
        }
        if (updatedAttempt.id == _selectedAttemptId.value) {
            _selectedAttempt.value = updatedAttempt
        }
    }

    fun clearTestAttempt(reagent: String, attemptId: Int) {
        val currentAttempts = _testAttempts.value[reagent] ?: emptyList()
        val updatedAttempts = currentAttempts.map { attempt ->
            if (attempt.id == attemptId) {
                attempt.copy(
                    flowRate = 0.0,
                    concentration = 0.0,
                    testTime = 0.0,
                    actualAmount = 0.0
                )
            } else {
                attempt
            }
        }
        _testAttempts.update { currentMap ->
            currentMap + (reagent to updatedAttempts)
        }

        if (attemptId == _selectedAttemptId.value) {
            _selectedAttempt.value = updatedAttempts.find { it.id == attemptId }
        }
    }


    // Удаление теста
    fun removeTestAttempt(reagent: String, attemptId: Int) {
        val currentAttempts = _testAttempts.value[reagent] ?: emptyList()
        val updatedAttempts = currentAttempts
            .filter { it.id != attemptId }
            .mapIndexed { index, attempt ->
                attempt.copy(id = index + 1)
            }

        _testAttempts.update { currentMap ->
            currentMap + (reagent to updatedAttempts)
        }

        // Если удалили выбранный тест, сбрасываем _selectedAttempt на первый тест
        if (attemptId == _selectedAttemptId.value) {
            _selectedAttemptId.value = updatedAttempts.firstOrNull()?.id
            _selectedAttempt.value = updatedAttempts.firstOrNull()
        }
    }

    fun selectAttempt(attemptId: Int) {
        _selectedAttemptId.value = attemptId
        val selectedReagent = _selectedReagentForTable.value
        val attempts = _testAttempts.value[selectedReagent] ?: emptyList()
        _selectedAttempt.value = attempts.find { it.id == attemptId }
    }

    // Добавление фотографии для реагента
    fun addPhotoForReagent(reagentName: String, photoUri: Uri) {
        val currentPhotos = _photosForReagents.value[reagentName] ?: emptyList()
        val attemptNumber = currentPhotos.size + 1 // Увеличиваем номер попытки
        val newPhoto =
            Photo(uri = photoUri, reagentName = reagentName, attemptNumber = attemptNumber)

        _photosForReagents.update { currentMap ->
            currentMap + (reagentName to (currentPhotos + newPhoto))
        }
        Log.e("gfhfghf", newPhoto.toString())
    }

    // Удаление фотографии для реагента
    fun removePhotoForReagent(reagentName: String, photo: Photo) {
        val currentPhotos = _photosForReagents.value[reagentName] ?: emptyList()
        val updatedPhotos = currentPhotos
            .filter { it != photo } // Удаляем фотографию
            .mapIndexed { index, p -> p.copy(attemptNumber = index + 1) } // Обновляем номера попыток

        _photosForReagents.update { currentMap ->
            currentMap + (reagentName to updatedPhotos)
        }
    }

    // Получение фотографий для конкретного реагента
    fun photosForReagent(reagentName: String): List<Photo> {
        return _photosForReagents.value[reagentName] ?: emptyList()
    }


    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val fields = getFields()
            val wells = getWells()
            val layers = getLayers()
            val customers = getCustomers()
            _fields.value = fields
            _wells.value = wells
            _layers.value = layers
            _customers.value = customers
            delay(1000)
            _isLoading.value = false
        }
    }

    fun onFieldSelected(field: Field) {
        _selectedField.value = field
        _fieldError.value = false
    }

    fun onWellSelected(well: Well) {
        _selectedWell.value = well
        _wellError.value = false
    }

    fun onLayerSelected(layer: Layer) {
        _selectedLayer.value = layer
    }

    fun onCustomerSelected(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun getSignature(): String? {
        return sharedPreferences.getString("signature", null)
    }

    suspend fun saveAllPhotosForReport(reportId: Int, context: Context) {
        return try {
            val allPhotos = _photosForReagents.value.values.flatten()

            allPhotos.forEach { photo ->

                val file = photo.uri.toFile(context = context)

                // Загружаем фото в Supabase
                insertPhotoReport.invoke(
                    reportId = reportId,
                    photoTypeName = photo.reagentName,
                    photoFile = file,
                    attemptNumber = photo.attemptNumber,
                    report_photo_table_name = "report_photos"
                )
            }
            _isLoading.value = false
            _isSuccess.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}


fun Uri.toFile(context: Context): File {
    val contentResolver = context.contentResolver
    val file = createTempFile(context)

    return try {
        contentResolver.openInputStream(this)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        throw IOException("Error converting URI to file", e)
    }
}

fun createTempFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}