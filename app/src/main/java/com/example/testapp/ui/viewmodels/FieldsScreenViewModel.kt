package com.example.testapp.ui.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.usecases.GetFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FieldsScreenViewModel @Inject constructor(
    private val getFields: GetFields,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _fields = MutableStateFlow<List<Field>?>(null)
    val fields: StateFlow<List<Field>?> = _fields

    private val _selectedFields = MutableStateFlow<List<Field>>(emptyList())
    val selectedFields: StateFlow<List<Field>> = _selectedFields

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    init {
        loadFields()
    }

    fun loadFields() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _fields.value = getFields.invoke()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initSelectedFields(initialFields: List<Field>) {
        _selectedFields.value = initialFields
    }

    fun toggleFieldSelection(field: Field) {
        _selectedFields.value = if (_selectedFields.value.contains(field)) {
            _selectedFields.value - field
        } else {
            _selectedFields.value + field
        }
    }

    fun resetSelectedFields(): List<Field> {
        _selectedFields.value = emptyList()
        return _selectedFields.value
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }
}