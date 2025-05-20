package com.example.testapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.models.ReportType
import com.example.testapp.domain.models.ReportTypeEnum
import com.example.testapp.domain.models.Well
import com.example.testapp.ui.viewmodels.FilterScreenViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FilterScreen(
    onBackPressed: () -> Unit,
    onDeleteFilters: () -> Unit,
    onSaveFilters: (ReportFilters) -> Unit,
    onFieldsClick: () -> Unit,
    onWellsClick: () -> Unit,
    onLayersClick: () -> Unit,
    onCustomersClick: () -> Unit,
    reportFilters: ReportFilters,
    viewModel: FilterScreenViewModel = hiltViewModel()
) {
    val selectedReportType by viewModel.selectedReportTypeId.collectAsState()
    var showDateRangePicker by remember { mutableStateOf(false) }

    val selectedDateRange by viewModel.selectedDateRange.collectAsState()
    val reportTypes by viewModel.reportTypes.collectAsState()

    // Initialize viewModel with initial filters
    LaunchedEffect(reportFilters) {
        viewModel.setFields(reportFilters.fields)
        viewModel.setWells(reportFilters.wells)
        viewModel.setLayers(reportFilters.layers)
        viewModel.setCustomers(reportFilters.customers)
        viewModel.setDateRange(reportFilters.dateRange)
        reportFilters.reportType?.let { enumType ->
            val reportType = reportTypes.find { it.id == enumType.id }
            viewModel.onReportTypeSelected(reportType?.id)
        } ?: viewModel.onReportTypeSelected(null)
        Log.d("Filters", "Restoring report type: ${reportFilters.reportType} -> id ${reportFilters.reportType?.id}")
        Log.d("Filters", "Found report type: ${reportTypes.find { it.id == reportFilters.reportType?.id }}")





    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Фильтры",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = {
                                viewModel.resetFilters()
                                onDeleteFilters()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                text = "Сбросить",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        }
    ) { paddingValues ->
        if (showDateRangePicker) {
            Box(modifier = Modifier.padding(paddingValues)) {
                DateRangePickerSample(
                    onDismiss = { showDateRangePicker = false },
                    onDateRangeSelected = { range ->
                        viewModel.setDateRange(range)
                        showDateRangePicker = false
                    }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    item {
                        ReportTypeFilterRow(
                            selectedTypeId = selectedReportType,
                            onTypeSelected = { typeId ->
                                viewModel.onReportTypeSelected(typeId)
                            },
                            reportTypes = reportTypes
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        FieldsRow(
                            onFieldsClick = onFieldsClick,
                            fields = reportFilters.fields
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        WellsRow(
                            onWellsClick = onWellsClick,
                            wells = reportFilters.wells
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        LayersRow(
                            onLayersClick = onLayersClick,
                            layers = reportFilters.layers
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        CustomersRow(
                            onCustomersClick = onCustomersClick,
                            customers = reportFilters.customers
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        DateRangeDisplayRow(
                            dateRange = selectedDateRange,
                            onClick = { showDateRangePicker = true }
                        )
                    }
                }

                Button(
                    onClick = {
                        onSaveFilters(viewModel.getCurrentFilters())
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(all = 16.dp)
                        .fillMaxWidth()
                        .zIndex(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = "Применить",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun ReportTypeFilterRow(
    selectedTypeId: Int? = null,
    onTypeSelected: (Int?) -> Unit,
    reportTypes: List<ReportType>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
    ) {

        val allTypes = listOf(ReportType(-1, "Все")) + reportTypes

        allTypes.forEach { reportType ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        reportType.id == -1 && selectedTypeId == null -> MaterialTheme.colorScheme.primary
                        reportType.id == selectedTypeId -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clickable {
                            onTypeSelected(if (reportType.id == -1) null else reportType.id)
                        }
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reportType.name,
                        color = when {
                            reportType.id == -1 && selectedTypeId == null -> MaterialTheme.colorScheme.onPrimary
                            reportType.id == selectedTypeId -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.outline
                        },
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

        }
    }
}


@Composable
fun FieldsRow(
    onFieldsClick: () -> Unit,
    fields: List<Field>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onFieldsClick()
                }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically


        ) {
            Text(
                text = "Месторождения",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge
            )

            if (fields.isEmpty()) {
                Text(
                    text = "Все",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelMedium
                )
            } else {
                val fieldsText = fields.joinToString(", ") { it.name }
                Text(
                    text = fieldsText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
            }
        }
    }

}


@Composable
fun WellsRow(
    onWellsClick: () -> Unit,
    wells: List<Well>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onWellsClick() }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Скважины",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge
            )

            if (wells.isEmpty()) {
                Text(
                    text = "Все",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelMedium
                )
            } else {
                val wellsText = wells.joinToString(", ") { it.wellNumber }
                Text(
                    text = wellsText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
            }
        }
    }
}

@Composable
fun LayersRow(
    onLayersClick: () -> Unit,
    layers: List<Layer>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onLayersClick() }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Пласты",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge
            )

            if (layers.isEmpty()) {
                Text(
                    text = "Все",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelMedium
                )
            } else {
                val layersText = layers.joinToString(", ") { it.layerName }
                Text(
                    text = layersText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
            }
        }
    }
}

@Composable
fun CustomersRow(
    onCustomersClick: () -> Unit,
    customers: List<Customer>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onCustomersClick() }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Заказчики",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge
            )

            if (customers.isEmpty()) {
                Text(
                    text = "Все",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelMedium
                )
            } else {
                val customersText = customers.joinToString(", ") { it.companyName }
                Text(
                    text = customersText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
            }
        }
    }
}

@Composable
fun DateRangeDisplayRow(
    dateRange: ClosedRange<LocalDate>?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Период",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Выбрать даты",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = dateRange?.let {
                        "${it.start.format(dateFormatter)} – ${it.endInclusive.format(dateFormatter)}"
                    } ?: "Не выбрано",
                    style = MaterialTheme.typography.labelMedium,
                    color = dateRange?.let {
                        MaterialTheme.colorScheme.primary
                    } ?: MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 180.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample(
    onDismiss: () -> Unit,
    onDateRangeSelected: (ClosedRange<LocalDate>) -> Unit
) {
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    SnackbarHost(hostState = snackState, Modifier.zIndex(1f))

    val state = rememberDateRangePickerState()
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onDismiss() }) {
                Icon(Icons.Filled.Close, contentDescription = "Localized description")
            }
            TextButton(
                onClick = {
                    snackScope.launch {
                        val range = state.selectedStartDateMillis!!..state.selectedEndDateMillis!!
                        snackState.showSnackbar("Saved range (timestamps): $range")
                    }
                    if (state.selectedStartDateMillis != null && state.selectedEndDateMillis != null) {
                        val startDate = Instant
                            .ofEpochMilli(state.selectedStartDateMillis!!)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val endDate = Instant
                            .ofEpochMilli(state.selectedEndDateMillis!!)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateRangeSelected(startDate..endDate)
                    }
                },
                enabled = state.selectedEndDateMillis != null
            ) {
                Text(text = "Сохранить")
            }
        }
        DateRangePicker(
            state = state,
            modifier = Modifier.weight(1f),
            title = {},
            headline = {
                Column(
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    if (state.selectedStartDateMillis != null) {
                        Text(
                            text = "Выбрано: ${state.selectedStartDateMillis!!.toFormattedDate()} - " +
                                    (state.selectedEndDateMillis?.toFormattedDate() ?: ""),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

            }
        )
    }
}

private fun Long.toFormattedDate(): String {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}

data class ButtonState(
    val text: String,
    val backgroundColor: Color,
    val textColor: Color
)