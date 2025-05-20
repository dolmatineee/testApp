package com.example.testapp.ui.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.usecases.GetCustomers
import com.example.testapp.domain.usecases.GetLayers
import com.example.testapp.domain.usecases.GetWells
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomersScreenViewModel @Inject constructor(
    private val getCustomers: GetCustomers,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _customers = MutableStateFlow<List<Customer>?>(null)
    val customers: StateFlow<List<Customer>?> = _customers

    private val _selectedCustomers = MutableStateFlow<List<Customer>>(emptyList())
    val selectedCustomers: StateFlow<List<Customer>> = _selectedCustomers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    init {
        loadCustomers()
    }

    fun loadCustomers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _customers.value = getCustomers.invoke()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initSelectedCustomers(initialCustomers: List<Customer>) {
        _selectedCustomers.value = initialCustomers
    }

    fun toggleCustomerSelection(customer: Customer) {
        _selectedCustomers.value = if (_selectedCustomers.value.contains(customer)) {
            _selectedCustomers.value - customer
        } else {
            _selectedCustomers.value + customer
        }
    }

    fun resetSelectedCustomers(): List<Customer> {
        _selectedCustomers.value = emptyList()
        return _selectedCustomers.value
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }
}