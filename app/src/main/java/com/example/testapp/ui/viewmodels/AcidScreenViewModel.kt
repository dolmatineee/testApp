package com.example.testapp.ui.viewmodels


import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.AcidReport
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Laboratorian
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.usecases.GetCustomers
import com.example.testapp.domain.usecases.GetFields
import com.example.testapp.domain.usecases.GetLaboratorians
import com.example.testapp.domain.usecases.GetLayers
import com.example.testapp.domain.usecases.GetWells
import com.example.testapp.domain.usecases.InsertAcidReport
import com.example.testapp.domain.usecases.InsertBlenderReport
import com.example.testapp.utils.PhotoType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AcidScreenViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val getFields: GetFields,
    private val getWells: GetWells,
    private val getLayers: GetLayers,
    private val getCustomers: GetCustomers,
    private val getLaboratorians: GetLaboratorians,
    private val insertAcidReport: InsertAcidReport
) : ViewModel() {

    private val _fields = MutableStateFlow<List<Field>>(emptyList())
    val fields: StateFlow<List<Field>> = _fields

    private val _wells = MutableStateFlow<List<Well>>(emptyList())
    val wells: StateFlow<List<Well>> = _wells

    private val _layers = MutableStateFlow<List<Layer>>(emptyList())
    val layers: StateFlow<List<Layer>> = _layers

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers

    private val _laboratorians = MutableStateFlow<List<Laboratorian>>(emptyList())
    val laboratorians: StateFlow<List<Laboratorian>> = _laboratorians

    private val _selectedField = MutableStateFlow<Field?>(null)
    val selectedField: StateFlow<Field?> = _selectedField

    private val _selectedWell = MutableStateFlow<Well?>(null)
    val selectedWell: StateFlow<Well?> = _selectedWell

    private val _selectedLayer = MutableStateFlow<Layer?>(null)
    val selectedLayer: StateFlow<Layer?> = _selectedLayer

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer

    private val _selectedLaboratorian = MutableStateFlow<Laboratorian?>(null)
    val selectedLaboratorian: StateFlow<Laboratorian?> = _selectedLaboratorian



    private val _photo5000General = MutableStateFlow<Uri?>(null)
    val photo5000General: StateFlow<Uri?> = _photo5000General

    private val _photo5000AfterPour_25_75 = MutableStateFlow<Uri?>(null)
    val photo5000AfterPour_25_75: StateFlow<Uri?> = _photo5000AfterPour_25_75

    private val _photo5000AfterPour_50_50 = MutableStateFlow<Uri?>(null)
    val photo5000AfterPour_50_50: StateFlow<Uri?> = _photo5000AfterPour_50_50

    private val _photo5000AfterPour_75_25 = MutableStateFlow<Uri?>(null)
    val photo5000AfterPour_75_25: StateFlow<Uri?> = _photo5000AfterPour_75_25

    private val _photo5000AfterPour_spent = MutableStateFlow<Uri?>(null)
    val photo5000AfterPour_spent: StateFlow<Uri?> = _photo5000AfterPour_spent

    // Фотографии для эмульсии 2000
    private val _photo2000General = MutableStateFlow<Uri?>(null)
    val photo2000General: StateFlow<Uri?> = _photo2000General

    private val _photo2000AfterPour_25_75 = MutableStateFlow<Uri?>(null)
    val photo2000AfterPour_25_75: StateFlow<Uri?> = _photo2000AfterPour_25_75

    private val _photo2000AfterPour_50_50 = MutableStateFlow<Uri?>(null)
    val photo2000AfterPour_50_50: StateFlow<Uri?> = _photo2000AfterPour_50_50

    private val _photo2000AfterPour_75_25 = MutableStateFlow<Uri?>(null)
    val photo2000AfterPour_75_25: StateFlow<Uri?> = _photo2000AfterPour_75_25

    private val _photo2000AfterPour_spent = MutableStateFlow<Uri?>(null)
    val photo2000AfterPour_spent: StateFlow<Uri?> = _photo2000AfterPour_spent

    private val _photoDensimeterConcentratedAcid = MutableStateFlow<Uri?>(null)
    val photoDensimeterConcentratedAcid: StateFlow<Uri?> = _photoDensimeterConcentratedAcid

    private val _photoDensimeterPreparedAcid = MutableStateFlow<Uri?>(null)
    val photoDensimeterPreparedAcid: StateFlow<Uri?> = _photoDensimeterPreparedAcid





    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

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
            val laboratorians = getLaboratorians()
            _fields.value = fields
            _wells.value = wells
            _layers.value = layers
            _customers.value = customers
            _laboratorians.value = laboratorians
            delay(1000)
            _isLoading.value = false
        }
    }

    fun setPhoto5000General(uri: Uri) {
        _photo5000General.value = uri
    }

    fun setPhoto5000AfterPour_25_75(uri: Uri) {
        _photo5000AfterPour_25_75.value = uri
    }

    fun setPhoto5000AfterPour_50_50(uri: Uri) {
        _photo5000AfterPour_50_50.value = uri
    }

    fun setPhoto5000AfterPour_75_25(uri: Uri) {
        _photo5000AfterPour_75_25.value = uri
    }

    fun setPhoto5000AfterPour_spent(uri: Uri) {
        _photo5000AfterPour_spent.value = uri
    }

    fun setPhoto2000General(uri: Uri) {
        _photo2000General.value = uri
    }

    fun setPhoto2000AfterPour_25_75(uri: Uri) {
        _photo2000AfterPour_25_75.value = uri
    }

    fun setPhoto2000AfterPour_50_50(uri: Uri) {
        _photo2000AfterPour_50_50.value = uri
    }

    fun setPhoto2000AfterPour_75_25(uri: Uri) {
        _photo2000AfterPour_75_25.value = uri
    }

    fun setPhoto2000AfterPour_spent(uri: Uri) {
        _photo2000AfterPour_spent.value = uri
    }

    fun setPhotoDensimeterConcentratedAcid(uri: Uri) {
        _photoDensimeterConcentratedAcid.value = uri
    }

    fun setPhotoDensimeterPreparedAcid(uri: Uri) {
        _photoDensimeterPreparedAcid.value = uri
    }

    // Методы для очистки фотографий
    fun clearPhoto5000General() {
        _photo5000General.value = null
    }

    fun clearPhoto5000AfterPour_25_75() {
        _photo5000AfterPour_25_75.value = null
    }

    fun clearPhoto5000AfterPour_50_50() {
        _photo5000AfterPour_50_50.value = null
    }

    fun clearPhoto5000AfterPour_75_25() {
        _photo5000AfterPour_75_25.value = null
    }

    fun clearPhoto5000AfterPour_spent() {
        _photo5000AfterPour_spent.value = null
    }

    fun clearPhoto2000General() {
        _photo2000General.value = null
    }

    fun clearPhoto2000AfterPour_25_75() {
        _photo2000AfterPour_25_75.value = null
    }

    fun clearPhoto2000AfterPour_50_50() {
        _photo2000AfterPour_50_50.value = null
    }

    fun clearPhoto2000AfterPour_75_25() {
        _photo2000AfterPour_75_25.value = null
    }

    fun clearPhoto2000AfterPour_spent() {
        _photo2000AfterPour_spent.value = null
    }

    fun clearPhotoDensimeterConcentratedAcid() {
        _photoDensimeterConcentratedAcid.value = null
    }

    fun clearPhotoDensimeterPreparedAcid() {
        _photoDensimeterPreparedAcid.value = null
    }

    // Метод для получения подписи
    fun getSignature(): String? {
        return sharedPreferences.getString("signature", null)
    }


    fun onFieldSelected(field: Field) {
        _selectedField.value = field
    }

    fun onWellSelected(well: Well) {
        _selectedWell.value = well
    }

    fun onLayerSelected(layer: Layer) {
        _selectedLayer.value = layer
    }

    fun onCustomerSelected(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun onLaboratorianSelected(laboratorian: Laboratorian) {
        _selectedLaboratorian.value = laboratorian
    }


    fun resetSuccessState() {
        _isSuccess.value = false
    }

    fun setSuccessState() {
        _isSuccess.value = true
    }

    suspend fun saveReportAndGetId(
        report: AcidReport,
        acidReportCode: String
    ): Int? {

        _isLoading.value = true

        val photos = mapOf(
            PhotoType.PHOTO_5000_GENERAL to _photo5000General.value,
            PhotoType.PHOTO_5000_AFTER_POUR_25_75 to _photo5000AfterPour_25_75.value,
            PhotoType.PHOTO_5000_AFTER_POUR_50_50 to _photo5000AfterPour_50_50.value,
            PhotoType.PHOTO_5000_AFTER_POUR_75_25 to _photo5000AfterPour_75_25.value,
            PhotoType.PHOTO_5000_AFTER_POUR_SPENT to _photo5000AfterPour_spent.value,
            PhotoType.PHOTO_2000_GENERAL to _photo2000General.value,
            PhotoType.PHOTO_2000_AFTER_POUR_25_75 to _photo2000AfterPour_25_75.value,
            PhotoType.PHOTO_2000_AFTER_POUR_50_50 to _photo2000AfterPour_50_50.value,
            PhotoType.PHOTO_2000_AFTER_POUR_75_25 to _photo2000AfterPour_75_25.value,
            PhotoType.PHOTO_2000_AFTER_POUR_SPENT to _photo2000AfterPour_spent.value,
            PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID to _photoDensimeterConcentratedAcid.value,
            PhotoType.PHOTO_DENSIMETER_PREPARED_ACID to _photoDensimeterPreparedAcid.value
        ).filterValues { it != null } as Map<PhotoType, Uri>




        val acidReportId = insertAcidReport(
            report = report,
            acidReportCode = acidReportCode,
            photos = photos
        )

        _isLoading.value = false

        return acidReportId
    }

}