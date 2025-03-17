package com.example.testapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberImagePainter
import com.example.testapp.R
import com.example.testapp.domain.models.EmulsionPhoto
import com.example.testapp.domain.models.Photo
import com.example.testapp.domain.models.Report
import com.example.testapp.ui.customs.CustomDropdownMenu
import com.example.testapp.ui.viewmodels.AcidScreenViewModel
import com.example.testapp.ui.viewmodels.BlenderScreenViewModel
import com.example.testapp.utils.generateBlenderReportBlender
import com.example.testapp.utils.toImageBitmap
import kotlinx.coroutines.launch
import java.io.File


/*@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)*/
/*@Composable
fun AcidScreen(
    viewModel: AcidScreenViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onSignatureCardClickListener: () -> Unit
) {
    val fields by viewModel.fields.collectAsState()
    val wells by viewModel.wells.collectAsState()
    val layers by viewModel.layers.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedField by viewModel.selectedField.collectAsState()
    val selectedWell by viewModel.selectedWell.collectAsState()
    val selectedLayer by viewModel.selectedLayer.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()


    var isFieldMenuExpanded by remember { mutableStateOf(false) }
    var isWellMenuExpanded by remember { mutableStateOf(false) }
    var isLayerMenuExpanded by remember { mutableStateOf(false) }
    var isCustomerMenuExpanded by remember { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }
    var selectedReagent by remember { mutableStateOf<String>("") }


    val signatureBase64 = viewModel.getSignature()
    val signatureBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

    val sharedPreferences =
        LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val employeeId = sharedPreferences.getInt("employeeId", 0)

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
                viewModel.setPhotoEmulsionPhoto5000(uri)
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
                    viewModel.setPhotoEmulsionPhoto5000(uri)
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
                        text = "Кислота",
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
                EmulsionTestPhotoSection(
                    photo = TODO(),
                    onAddPhoto = TODO(),
                    onRemovePhoto = TODO(),
                    modifier = TODO()
                )
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
                            val reportFile = generateBlenderReportBlender(
                                context = context,
                                customer = selectedCustomer!!,
                                field = selectedField!!,
                                layer = selectedLayer!!,
                                well = selectedWell!!,
                                testAttemptsMap = testAttempts,
                                photos = photosForReagents.flatMap { it.value },
                                signatureBitmap = signatureBitmap.value!!
                            )

                            viewModel.viewModelScope.launch {
                                // Получаем ID реагентов по их именам
                                val reagentIds = testAttempts.keys.associateWith { reagentName ->
                                    viewModel.getReagentIdByName(reagentName)
                                        ?: throw IllegalStateException("Reagent $reagentName not found")
                                }

                                val reportId = viewModel.saveReportAndGetId(
                                    report = Report(
                                        employeeId = employeeId,
                                        fieldId = selectedField!!.id,
                                        wellId = selectedWell!!.id,
                                        layerId = selectedLayer!!.id,
                                        customerId = selectedCustomer!!.id,
                                        createdAt = toString(),
                                        reportName = "blender",
                                        reagents = emptyList()
                                    ),
                                    file = reportFile
                                )
                                Log.e("gfhfgh", reportId.toString())

                                // Передаем reagentIds в метод convertToReagents
                                viewModel.updateReportWithReagents(
                                    reportId!!,
                                    convertToReagents(
                                        testAttempts,
                                        reportId,
                                        reagentIds
                                    )
                                )
                            }
                        } else {
                            // Обработка случая, когда не все поля выбраны
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = "Сгенерировать отчет",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }

        }

        if (showPhotoSourceDialog) {
            AlertDialog(
                shape = RoundedCornerShape(12.dp),
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = {
                    // Закрываем диалоговое окно при нажатии вне его или на кнопку "Отмена"
                    showPhotoSourceDialog = false
                },
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Выберите источник фотографии",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .dashedBorder(1.dp, MaterialTheme.colorScheme.primary, 8.dp),
                            colors = CardDefaults.cardColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.background
                            ),
                            onClick = {
                                galleryLauncher.launch("image/*")
                                showPhotoSourceDialog = false
                            }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_insert_photo_24),
                                    contentDescription = "Добавить фото"
                                )
                            }


                        }

                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .dashedBorder(1.dp, MaterialTheme.colorScheme.primary, 8.dp),
                            colors = CardDefaults.cardColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.background
                            ),
                            onClick = {
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
                            }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    painter = painterResource(R.drawable.baseline_photo_camera_24),
                                    contentDescription = "Добавить фото"
                                )

                            }


                        }
                    }
                },
                confirmButton = {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            // Закрываем диалоговое окно
                            showPhotoSourceDialog = false
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 4.dp),
                            text = "Отмена",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }


    }
}*/


@Composable
fun EmulsionTestPhotoSection(
    photo: Uri?,
    onAddPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
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
            // Отображение фотографии
            if (photo != null) {
                EmulsionPhotoCard(
                    photo = photo,
                    onRemovePhoto = onRemovePhoto,
                    modifier = Modifier.height(200.dp)
                )
            } else {
                AddEmulsionPhotoCard(
                    onAddPhoto = onAddPhoto,
                    modifier = Modifier.height(200.dp)
                )
            }

            // Текст под фотографией
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Фотография теста на совместимость и распад эмульсии 5000 мг/л",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun AddEmulsionPhotoCard(
    onAddPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp),
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
fun EmulsionPhotoCard(
    photo: Uri,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberImagePainter(data = photo),
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
        }
    }
}

*/