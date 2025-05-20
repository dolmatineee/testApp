package com.example.testapp.ui.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.usecases.GetLayers
import com.example.testapp.domain.usecases.GetWells
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LayersScreenViewModel @Inject constructor(
    private val getLayers: GetLayers,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _layers = MutableStateFlow<List<Layer>?>(null)
    val layers: StateFlow<List<Layer>?> = _layers

    private val _selectedLayers = MutableStateFlow<List<Layer>>(emptyList())
    val selectedLayers: StateFlow<List<Layer>> = _selectedLayers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    init {
        loadLayers()
    }

    fun loadLayers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _layers.value = getLayers.invoke()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initSelectedLayers(initialLayers: List<Layer>) {
        _selectedLayers.value = initialLayers
    }

    fun toggleLayerSelection(layer: Layer) {
        _selectedLayers.value = if (_selectedLayers.value.contains(layer)) {
            _selectedLayers.value - layer
        } else {
            _selectedLayers.value + layer
        }
    }

    fun resetSelectedLayers(): List<Layer> {
        _selectedLayers.value = emptyList()
        return _selectedLayers.value
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }
}