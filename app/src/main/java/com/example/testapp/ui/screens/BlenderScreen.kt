package com.example.testapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Photo
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.TestAttempt
import com.example.testapp.ui.customs.CustomDropdownMenu
import com.example.testapp.ui.customs.CustomTextField
import com.example.testapp.ui.viewmodels.BlenderScreenViewModel
import com.example.testapp.utils.generateReport
import com.example.testapp.utils.shareReport
import com.example.testapp.utils.toImageBitmap
import java.io.File


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlenderScreen(
    viewModel: BlenderScreenViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onSignatureCardClickListener: () -> Unit
) {
    val fields by viewModel.fields.collectAsState()
    val wells by viewModel.wells.collectAsState()
    val layers by viewModel.layers.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val selectedField by viewModel.selectedField.collectAsState()
    val selectedWell by viewModel.selectedWell.collectAsState()
    val selectedLayer by viewModel.selectedLayer.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val photosForReagents by viewModel.photosForReagents.collectAsState()


    var isFieldMenuExpanded by remember { mutableStateOf(false) }
    var isWellMenuExpanded by remember { mutableStateOf(false) }
    var isLayerMenuExpanded by remember { mutableStateOf(false) }
    var isCustomerMenuExpanded by remember { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }
    var selectedReagent by remember { mutableStateOf<String>("") }

    val reagents = viewModel.reagents
    val selectedReagentForTable by viewModel.selectedReagentForTable.collectAsState()
    val selectedAttemptId by viewModel.selectedAttemptId.collectAsState()
    val testAttempts by viewModel.testAttempts.collectAsState()
    val selectedAttempt by viewModel.selectedAttempt.collectAsState()

    val signatureBase64 = viewModel.getSignature()
    val signatureBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(signatureBase64) {
        if (signatureBase64 != null) {
            signatureBitmap.value = signatureBase64.toImageBitmap()
        }
    }


    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                // Передаем URI фотографии и имя реагента в ViewModel
                viewModel.addPhotoForReagent(selectedReagent, uri)
            }
        }
    )

    // Функция для создания уникального файла фотографии
    fun createUniquePhotoFile(): File {
        return File.createTempFile(
            "photo_${System.currentTimeMillis()}", // Уникальное имя файла
            ".jpg", // Расширение файла
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) // Директория для сохранения
        )
    }

// Переменная для хранения URI текущего файла
    var photoUriForCamera by remember { mutableStateOf<Uri?>(null) }

// Launcher для съемки фотографии
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // Получаем URI нового файла и передаем его в ViewModel с именем реагента
                photoUriForCamera?.let { uri ->
                    viewModel.addPhotoForReagent(selectedReagent, uri)
                    Log.e("fghfghgh", "кайффффф")
                }
            }
        }
    )

    // Функция для запуска камеры
    fun launchCamera() {
        val photoFile = createUniquePhotoFile()
        photoUriForCamera = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
        // Проверяем, что photoUriForCamera не null
        photoUriForCamera?.let { uri ->
            cameraLauncher.launch(uri)
        } ?: run {
            // Обработка случая, если photoUriForCamera равен null
            Toast.makeText(context, "Не удалось создать файл для фотографии", Toast.LENGTH_SHORT)
                .show()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Разрешение предоставлено, запускаем камеру
            photoUriForCamera?.let { cameraLauncher.launch(it) }
        } else {
            // Разрешение не предоставлено, покажите пользователю сообщение
            Toast.makeText(context, "Разрешение на камеру отклонено", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Блендер",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Text(
                    text = "Титульник",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                CustomDropdownMenu(
                    label = "Месторождение",
                    items = fields.map { it.name },
                    selectedItem = selectedField?.name,
                    isExpanded = isFieldMenuExpanded,
                    onExpandedChange = { isFieldMenuExpanded = it },
                    onItemSelected = { fieldName ->
                        viewModel.onFieldSelected(fields.first { it.name == fieldName })
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                // Выбор скважины
                CustomDropdownMenu(
                    label = "Скважина",
                    items = wells.map { it.wellNumber },
                    selectedItem = selectedWell?.wellNumber,
                    isExpanded = isWellMenuExpanded,
                    onExpandedChange = { isWellMenuExpanded = it },
                    onItemSelected = { wellNumber ->
                        viewModel.onWellSelected(wells.first { it.wellNumber == wellNumber })
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                // Выбор скважины
                CustomDropdownMenu(
                    label = "Пласт",
                    items = layers.map { it.layerName },
                    selectedItem = selectedLayer?.layerName,
                    isExpanded = isLayerMenuExpanded,
                    onExpandedChange = { isLayerMenuExpanded = it },
                    onItemSelected = { layerName ->
                        viewModel.onLayerSelected(layers.first { it.layerName == layerName })
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                // Выбор скважины
                CustomDropdownMenu(
                    label = "Скважина",
                    items = customers.map { it.companyName },
                    selectedItem = selectedCustomer?.companyName,
                    isExpanded = isCustomerMenuExpanded,
                    onExpandedChange = { isCustomerMenuExpanded = it },
                    onItemSelected = { companyName ->
                        viewModel.onCustomerSelected(customers.first { it.companyName == companyName })
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                Text(
                    text = "Фотоотчет",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                ReagentPhotoSection(
                    reagentName = "сшивателя ТТ ВС марка 1",
                    photos = photosForReagents["сшивателя ТТ ВС марка 1"] ?: emptyList(),
                    onAddPhoto = { reagent ->
                        selectedReagent = reagent // Устанавливаем выбранный реагент
                        showPhotoSourceDialog = true // Показываем диалоговое окно
                    },
                    onRemovePhoto = { photo ->
                        viewModel.removePhotoForReagent(
                            "сшивателя ТТ ВС марка 1",
                            photo
                        )
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                // Карточка для активатора брейкера ТТ АВ
                ReagentPhotoSection(
                    reagentName = "активатора брейкера ТТ АВ",
                    photos = photosForReagents["активатора брейкера ТТ АВ"] ?: emptyList(),
                    onAddPhoto = { reagent ->
                        selectedReagent = reagent // Устанавливаем выбранный реагент
                        showPhotoSourceDialog = true // Показываем диалоговое окно
                    },
                    onRemovePhoto = { photo ->
                        viewModel.removePhotoForReagent(
                            "активатора брейкера ТТ АВ",
                            photo
                        )
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                // Карточка для брейкера ТТ ВА марка АР
                ReagentPhotoSection(
                    reagentName = "брейкера ТТ ВА марка АР",
                    photos = photosForReagents["брейкера ТТ ВА марка АР"] ?: emptyList(),
                    onAddPhoto = { reagent ->
                        selectedReagent = reagent // Устанавливаем выбранный реагент
                        showPhotoSourceDialog = true // Показываем диалоговое окно
                    },
                    onRemovePhoto = { photo ->
                        viewModel.removePhotoForReagent(
                            "брейкера ТТ ВА марка АР",
                            photo
                        )
                    }
                )

            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                Text(
                    text = "Таблица",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                ReagentTable(
                    reagents = reagents,
                    selectedReagent = selectedReagentForTable,
                    onReagentSelected = { viewModel.selectReagent(it) },
                    testAttempts = testAttempts[selectedReagentForTable] ?: emptyList(),
                    selectedAttempt = selectedAttempt,
                    onTestAttemptUpdated = {
                        viewModel.updateTestAttempt(
                            selectedReagentForTable,
                            it
                        )
                    },
                    onTestAttemptAdded = { viewModel.addTestAttempt(selectedReagentForTable) },
                    onTestAttemptRemoved = {
                        viewModel.removeTestAttempt(
                            selectedReagentForTable,
                            it
                        )
                    },
                    onAttemptSelected = { viewModel.selectAttempt(it) },
                    onClear = {viewModel.clearTestAttempt(selectedReagentForTable, it.id)}
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                Text(
                    text = "Подпись",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier
                        .clickable { onSignatureCardClickListener() }
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (signatureBitmap.value != null) {
                            Image(
                                bitmap = signatureBitmap.value!!,
                                contentDescription = "Подпись",
                                modifier = Modifier
                                    .size(200.dp)
                                    .background(Color.Transparent)
                            )
                        } else {
                            Text(
                                text = "Добавьте подпись",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {

                Button(
                    onClick = {
                        if (selectedField != null && selectedWell != null && selectedLayer != null && selectedCustomer != null) {
                            val reportFile = generateReport(
                                context = context,
                                customer = selectedCustomer!!, // Замените на реальные данные
                                field = selectedField!!,
                                layer = selectedLayer!!, // Замените на реальные данные
                                well = selectedWell!!,
                                reagents = emptyList(),
                                attempts = emptyList(),
                                photos = photosForReagents.flatMap { it.value },
                                signatureBitmap = signatureBitmap.value!!
                            )
                            shareReport(reportFile, context)
                        } else {
                            // Показать ошибку, если не выбраны месторождение или скважина
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сгенерировать отчет")
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }

        }

        if (showPhotoSourceDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Закрываем диалоговое окно при нажатии вне его или на кнопку "Отмена"
                    showPhotoSourceDialog = false
                },
                title = {
                    Text(text = "Выберите источник фотографии")
                },
                text = {
                    Column {
                        Button(
                            onClick = {
                                // Запускаем выбор фотографии из галереи
                                galleryLauncher.launch("image/*")
                                showPhotoSourceDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Выбрать из галереи")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                // Запускаем камеру для съемки фотографии
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    launchCamera()
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                                showPhotoSourceDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Сделать фото")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Закрываем диалоговое окно
                            showPhotoSourceDialog = false
                        }
                    ) {
                        Text(text = "Отмена")
                    }
                }
            )
        }


    }
}


@Composable
fun ReagentPhotoSection(
    reagentName: String,
    photos: List<Photo>,
    onAddPhoto: (String) -> Unit, // Теперь передаем имя реагента
    onRemovePhoto: (Photo) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .dashedBorder(1.dp, MaterialTheme.colorScheme.primary, 8.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Отображение фотографий в сетке
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 132.dp, max = 264.dp)
            ) {
                items(photos) { photo ->
                    PhotoCard(
                        photo = photo,
                        onRemovePhoto = { onRemovePhoto(photo) }
                    )
                }
                item {
                    AddPhotoCard(
                        onAddPhoto = { onAddPhoto(reagentName) } // Передаем имя реагента
                    )
                }
            }

            // Текст под фотографиями
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Фотография тестирование основного насоса подачи $reagentName",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )
        }
    }
}


fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

        this.then(
            Modifier.drawWithCache {
                onDrawBehind {
                    val stroke = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    drawRoundRect(
                        color = color,
                        style = stroke,
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                }
            }
        )
    }
)

@Composable
fun AddPhotoCard(
    onAddPhoto: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onAddPhoto
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить фото"
            )
        }
    }
}

@Composable
fun PhotoCard(
    photo: Photo,
    onRemovePhoto: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberImagePainter(data = photo.uri),
                contentDescription = "Фотография",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            IconButton(
                onClick = onRemovePhoto,
                modifier = Modifier.align(Alignment.TopEnd),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = "Удалить фото")
            }
            Text(
                text = "Попытка №${photo.attemptNumber}",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
fun ReagentTable(
    reagents: List<String>,
    selectedReagent: String,
    onReagentSelected: (String) -> Unit,
    testAttempts: List<TestAttempt>,
    selectedAttempt: TestAttempt?,
    onTestAttemptUpdated: (TestAttempt) -> Unit,
    onTestAttemptAdded: () -> Unit,
    onTestAttemptRemoved: (Int) -> Unit,
    onAttemptSelected: (Int) -> Unit,
    onClear: (TestAttempt) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Навигация по реагентам
        ReagentNavigationBar(
            reagents = reagents,
            selectedReagent = selectedReagent,
            onReagentSelected = onReagentSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Список тестов в LazyVerticalGrid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 1000.dp)
        ) {
            items(testAttempts) { attempt ->
                TestAttemptCard(
                    attempt = attempt,
                    isSelected = attempt.id == selectedAttempt?.id,
                    onClick = {
                        if (attempt.id == selectedAttempt?.id) {
                            onAttemptSelected(-1)
                        } else {
                            onAttemptSelected(attempt.id)
                        }
                              }
                    ,
                    onDelete = { onTestAttemptRemoved(attempt.id) }
                )
            }
            item {
                AddTestButton(onClick = onTestAttemptAdded)
            }
        }


        Spacer(modifier = Modifier.height(8.dp))


        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surface)
        )


        Spacer(modifier = Modifier.height(8.dp))


        // Поля для редактирования выбранного теста
        selectedAttempt?.let { attempt ->
            TestAttemptForm(
                attempt = attempt,
                onSave = { updatedAttempt ->
                    onTestAttemptUpdated(updatedAttempt)
                },
                onClear = { clearAttempt ->
                    onClear(clearAttempt)
                }
            )
        }
    }
}

@Composable
fun ReagentNavigationBar(
    reagents: List<String>,
    selectedReagent: String,
    onReagentSelected: (String) -> Unit
) {
    val selectedIndex = reagents.indexOf(selectedReagent)


    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопка для перехода к предыдущему реагенту
        if (selectedIndex > 0) {
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { onReagentSelected(reagents[selectedIndex - 1]) }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Предыдущий реагент")
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp)) // Заглушка для выравнивания
        }


        Text(
            text = selectedReagent,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(8.dp)
        )


        // Кнопка для перехода к следующему реагенту
        if (selectedIndex < reagents.size - 1) {
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { onReagentSelected(reagents[selectedIndex + 1]) })
            {
                Icon(Icons.Default.ArrowForward, contentDescription = "Следующий реагент")
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp)) // Заглушка для выравнивания
        }
    }
}

@Composable
fun TestAttemptCard(
    attempt: TestAttempt,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .padding(4.dp)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),


        ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(8.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "Тест №${attempt.id}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(

                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(0.6f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = "Удалить тест"
                )
            }
        }
    }
}

@Composable
fun AddTestButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Box(
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить тест")
        }
    }
}

@Composable
fun TestAttemptForm(
    attempt: TestAttempt,
    onSave: (TestAttempt) -> Unit,
    onClear: (TestAttempt) -> Unit
) {
    var flowRate by remember { mutableStateOf(attempt.flowRate.toString()) }
    var concentration by remember { mutableStateOf(attempt.concentration.toString()) }
    var testTime by remember { mutableStateOf(attempt.testTime.toString()) }
    var actualAmount by remember { mutableStateOf(attempt.actualAmount.toString()) }

    LaunchedEffect(attempt) {
        flowRate = attempt.flowRate.toString()
        concentration = attempt.concentration.toString()
        testTime = attempt.testTime.toString()
        actualAmount = attempt.actualAmount.toString()
    }

    val clearAttempt = attempt.copy(
        flowRate = 0.0,
        concentration =  0.0,
        testTime = 0.0,
        actualAmount =  0.0
    )


    fun updateAttempt() {
        val updatedAttempt = attempt.copy(
            flowRate = flowRate.toDoubleOrNull() ?: 0.0,
            concentration = concentration.toDoubleOrNull() ?: 0.0,
            testTime = testTime.toDoubleOrNull() ?: 0.0,
            actualAmount = actualAmount.toDoubleOrNull() ?: 0.0
        )
        onSave(updatedAttempt)
    }

    LaunchedEffect(flowRate, concentration, testTime, actualAmount) {
        updateAttempt()
    }

    Column(modifier = Modifier.padding(top = 4.dp)) {

        CustomTextField(
            value = flowRate,
            onValueChange = { flowRate = it },
            label = "Расход",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = concentration,
            onValueChange = { concentration = it },
            label = "Концентрация",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))


        CustomTextField(
            value = testTime,
            onValueChange = { testTime = it },
            label = "Время теста",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))


        CustomTextField(
            value = actualAmount,
            onValueChange = { actualAmount = it },
            label = "Факт",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                flowRate = "0"
                concentration = "0"
                testTime = "0"
                actualAmount = "0"
                onClear(clearAttempt)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Очистить")
        }
    }
}




