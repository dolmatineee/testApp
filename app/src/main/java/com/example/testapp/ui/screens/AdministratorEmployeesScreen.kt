package com.example.testapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Position
import com.example.testapp.ui.customs.CustomTextField
import com.example.testapp.ui.viewmodels.AdministratorEmployeesViewModel
import com.example.testapp.ui.viewmodels.AdministratorStatsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesScreen() {
    val viewModel: AdministratorEmployeesViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Сотрудники",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                state.isLoading -> CenterProgress()
                state.error != null -> ErrorMessage(state.error!!) { viewModel.loadEmployees() }
                else -> EmployeeList(
                    employees = state.employees,
                    positions = state.positions,
                    onEmployeeClick = { employee ->
                        viewModel.selectEmployeeForEdit(employee)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Диалог редактирования
            state.selectedEmployee?.let { employee ->
                EditEmployeeDialog(
                    employee = employee,
                    positions = state.positions,
                    onDismiss = { viewModel.clearSelection() },
                    onSave = { updatedEmployee ->
                        viewModel.updateEmployee(updatedEmployee)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmployeeList(
    employees: List<Employee>,
    positions: List<Position>,
    onEmployeeClick: (Employee) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            HeaderRow()
        }

        items(employees) { employee ->
            EmployeeRow(
                employee = employee,
                positionName = positions.find { it.positionName == employee.position }?.positionName ?: "",
                onClick = { onEmployeeClick(employee) }
            )
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("ФИО", style = MaterialTheme.typography.bodyLarge)
        Text("Должность", style = MaterialTheme.typography.bodyLarge)
    }
    Divider()
}

@Composable
private fun EmployeeRow(
    employee: Employee,
    positionName: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = employee.fullName,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = if (positionName.length > 15) "${positionName.take(15)}..." else positionName,
            style = MaterialTheme.typography.labelMedium
        )
        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
    }
    Divider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditEmployeeDialog(
    employee: Employee,
    positions: List<Position>,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf(TextFieldValue(employee.fullName)) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue(employee.phoneNumber)) }
    var password by remember { mutableStateOf(TextFieldValue(employee.password)) }
    var selectedPosition by remember { mutableStateOf(employee.position) }
    var expanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Редактирование сотрудника",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = fullName.text,
                onValueChange = { fullName = TextFieldValue(it) },
                label = "ФИО",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = phoneNumber.text,
                onValueChange = { phoneNumber = TextFieldValue(it) },
                label = "Телефон",
                modifier = Modifier.fillMaxWidth(),
                onlyNumbers = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = password.text,
                onValueChange = { password = TextFieldValue(it) },
                label = "Пароль",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Выпадающий список для должности
            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = selectedPosition,
                    onValueChange = {},
                    label = { Text("Должность") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    positions.forEach { position ->
                        DropdownMenuItem(
                            text = { Text(position.positionName) },
                            onClick = {
                                selectedPosition = position.positionName
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Отмена")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        val updatedEmployee = employee.copy(
                            fullName = fullName.text,
                            phoneNumber = phoneNumber.text,
                            password = password.text,
                            position = selectedPosition
                        )
                        onSave(updatedEmployee)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}

@Composable
private fun CenterProgress() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        repeat(20) {
            ShimmerLoadingItem(32.dp, 8.dp)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ErrorMessage(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(error, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}