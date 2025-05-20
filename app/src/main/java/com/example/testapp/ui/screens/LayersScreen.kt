package com.example.testapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.models.Well
import com.example.testapp.ui.customs.CustomTextField
import com.example.testapp.ui.viewmodels.FieldsScreenViewModel
import com.example.testapp.ui.viewmodels.LayersScreenViewModel
import com.example.testapp.ui.viewmodels.WellsScreenViewModel
import com.example.testapp.utils.PhoneNumberVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayersScreen(
    viewModel: LayersScreenViewModel = hiltViewModel(),
    reportFilters: ReportFilters,
    onBackPressed: () -> Unit,
    onSaveLayers: (List<Layer>) -> Unit,
) {
    val layers by viewModel.layers.collectAsState(initial = listOf())
    val selectedLayers by viewModel.selectedLayers.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val searchText by viewModel.searchText.collectAsState()

    if (reportFilters.layers.isNotEmpty()) {
        viewModel.initSelectedLayers(reportFilters.layers)
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
                            text = "Пласты",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = {
                                val newLayers = viewModel.resetSelectedLayers()
                                reportFilters.layers = newLayers
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                text = "Сбросить",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed()
                        }
                    ) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Button(
                onClick = {
                    onSaveLayers(selectedLayers)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(all = 16.dp)
                    .fillMaxWidth()
                    .zIndex(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    text = "Применить",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CustomTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    value = searchText,
                    onValueChange = { viewModel.updateSearchText(it) },
                    label = "Поиск"
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    repeat(10) {
                        ShimmerLoadingItem(32.dp, 8.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                } else {
                    val filteredLayers = layers?.filter { layer ->
                        layer.layerName.contains(searchText, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        filteredLayers?.let { layersList ->
                            itemsIndexed(layersList) { index, layer ->
                                LayerItem(
                                    layer = layer,
                                    isSelected = selectedLayers.contains(layer),
                                    onSelect = { viewModel.toggleLayerSelection(layer) },
                                    isLastItem = index == layersList.lastIndex
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LayerItem(
    layer: Layer,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isLastItem: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect() }
                .background(
                    if (isSelected) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.background
                )
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .height(32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = layer.layerName,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onBackground
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (!isLastItem) {
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant
            )
        } else {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}