package com.example.testapp.ui.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.usecases.GetWells
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellsScreenViewModel @Inject constructor(
    private val getWells: GetWells,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _wells = MutableStateFlow<List<Well>?>(null)
    val wells: StateFlow<List<Well>?> = _wells

    private val _selectedWells = MutableStateFlow<List<Well>>(emptyList())
    val selectedWells: StateFlow<List<Well>> = _selectedWells

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
                _wells.value = getWells.invoke()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initSelectedFields(initialWells: List<Well>) {
        _selectedWells.value = initialWells
    }

    fun toggleFieldSelection(well: Well) {
        _selectedWells.value = if (_selectedWells.value.contains(well)) {
            _selectedWells.value - well
        } else {
            _selectedWells.value + well
        }
    }

    fun resetSelectedWells(): List<Well> {
        _selectedWells.value = emptyList()
        return _selectedWells.value
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }
}