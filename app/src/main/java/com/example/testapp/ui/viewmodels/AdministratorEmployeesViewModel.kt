package com.example.testapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Position
import com.example.testapp.domain.usecases.GetAllEmployees
import com.example.testapp.domain.usecases.GetAllPositions
import com.example.testapp.domain.usecases.UpdateEmployee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdministratorEmployeesViewModel @Inject constructor(
    private val getAllEmployees: GetAllEmployees,
    private val getAllPositions: GetAllPositions,
    private val updateEmployee1: UpdateEmployee
) : ViewModel() {
    private val _state = MutableStateFlow(EmployeesState())
    val state: StateFlow<EmployeesState> = _state.asStateFlow()

    init {
        loadEmployees()
        loadPositions()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val employees = getAllEmployees()
                _state.value = _state.value.copy(
                    employees = employees,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Не удалось загрузить сотрудников",
                    isLoading = false
                )
            }
        }
    }

    fun loadPositions() {
        viewModelScope.launch {
            try {
                val positions = getAllPositions()
                _state.value = _state.value.copy(positions = positions)
            } catch (e: Exception) {
                // Можно добавить обработку ошибки
            }
        }
    }

    fun selectEmployeeForEdit(employee: Employee) {
        _state.value = _state.value.copy(selectedEmployee = employee)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedEmployee = null)
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val success = updateEmployee1(employee)
                if (success) {
                    loadEmployees() // Перезагружаем список после обновления
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    selectedEmployee = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Не удалось обновить сотрудника",
                    isLoading = false
                )
            }
        }
    }
}

data class EmployeesState(
    val employees: List<Employee> = emptyList(),
    val positions: List<Position> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedEmployee: Employee? = null
)