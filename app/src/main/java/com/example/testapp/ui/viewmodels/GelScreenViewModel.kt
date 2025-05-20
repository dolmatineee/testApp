package com.example.testapp.ui.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.AcidReport
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.GelReport
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Photo
import com.example.testapp.domain.models.TestAttempt
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.usecases.GetCustomers
import com.example.testapp.domain.usecases.GetFields
import com.example.testapp.domain.usecases.GetLayers
import com.example.testapp.domain.usecases.GetWells
import com.example.testapp.domain.usecases.InsertBlenderReport
import com.example.testapp.domain.usecases.InsertGelReport
import com.example.testapp.domain.usecases.InsertPhotoReport
import com.example.testapp.utils.PhotoType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GelScreenViewModel @Inject constructor(
    private val getFields: GetFields,
    private val getWells: GetWells,
    private val getLayers: GetLayers,
    private val getCustomers: GetCustomers,
    private val insertGelReport: InsertGelReport,
    private val sharedPreferences: SharedPreferences,
    private val insertPhotoReport: InsertPhotoReport,
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


    private val _testAttempts = MutableStateFlow<Map<String, List<TestAttempt>>>(emptyMap())
    val testAttempts: StateFlow<Map<String, List<TestAttempt>>> get() = _testAttempts

    private val _selectedAttemptId = MutableStateFlow<Int?>(null)
    val selectedAttemptId: StateFlow<Int?> get() = _selectedAttemptId

    private val _selectedAttempt = MutableStateFlow<TestAttempt?>(null)
    val selectedAttempt: StateFlow<TestAttempt?> get() = _selectedAttempt


    private val _photosForGels= MutableStateFlow<Map<String, List<Photo>>>(emptyMap())
    val photosForGels: StateFlow<Map<String, List<Photo>>> get() = _photosForGels

    private val _photoSampling = MutableStateFlow<Uri?>(null)
    val photoSampling: StateFlow<Uri?> = _photoSampling


    fun addPhotoForReagent(reagentName: String, photoUri: Uri) {
        val currentPhotos = _photosForGels.value[reagentName] ?: emptyList()
        val attemptNumber = currentPhotos.size + 1 // Увеличиваем номер попытки
        val newPhoto =
            Photo(uri = photoUri, reagentName = reagentName, attemptNumber = attemptNumber)

        _photosForGels.update { currentMap ->
            currentMap + (reagentName to (currentPhotos + newPhoto))
        }
        Log.e("gfhfghf", newPhoto.toString())
    }

    // Удаление фотографии для реагента
    fun removePhotoForReagent(reagentName: String, photo: Photo) {
        val currentPhotos = _photosForGels.value[reagentName] ?: emptyList()
        val updatedPhotos = currentPhotos
            .filter { it != photo } // Удаляем фотографию
            .mapIndexed { index, p -> p.copy(attemptNumber = index + 1) } // Обновляем номера попыток

        _photosForGels.update { currentMap ->
            currentMap + (reagentName to updatedPhotos)
        }
    }

    fun setPhotoSampling(uri: Uri) {
        _photoSampling.value = uri
    }

    fun clearPhotoSampling() {
        _photoSampling.value = null
    }

    fun resetSuccessState() {
        _isSuccess.value = false
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



    suspend fun saveReportAndGetId(
        report: GelReport,
        gelReportCode: String,
        photoSamplingUri: Uri
    ): Int? {

        _isLoading.value = true

        val photos = mapOf(
            PhotoType.PHOTO_SAMPLING to _photoSampling.value,
        ).filterValues { it != null } as Map<PhotoType, Uri>




        val gelReportId = insertGelReport(
            report = report,
            gelReportCode = gelReportCode,
            photoSamplingUri = photoSamplingUri
        )



        return gelReportId
    }

    suspend fun saveAllPhotosForReport(reportId: Int, context: Context) {
        return try {
            val allPhotos = _photosForGels.value.values.flatten()

            allPhotos.forEach { photo ->

                val file = photo.uri.toFile(context = context)

                // Загружаем фото в Supabase
                insertPhotoReport.invoke(
                    reportId = reportId,
                    photoTypeName = photo.reagentName,
                    photoFile = file,
                    attemptNumber = photo.attemptNumber,
                    report_photo_table_name = "gel_report_photos"
                )
            }
            _isLoading.value = false
            _isSuccess.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}