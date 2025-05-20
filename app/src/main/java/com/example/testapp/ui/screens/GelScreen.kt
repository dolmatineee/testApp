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
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.R
import com.example.testapp.domain.models.Photo
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.GelReport
import com.example.testapp.ui.customs.CustomDropdownMenu
import com.example.testapp.ui.viewmodels.GelScreenViewModel
import com.example.testapp.utils.PhotoType
import com.example.testapp.utils.copyToClipboard
import com.example.testapp.utils.transliterate
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GelScreen(
    viewModel: GelScreenViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val fields by viewModel.fields.collectAsState()
    val wells by viewModel.wells.collectAsState()
    val layers by viewModel.layers.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val selectedField by viewModel.selectedField.collectAsState()
    val selectedWell by viewModel.selectedWell.collectAsState()
    val selectedLayer by viewModel.selectedLayer.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val photosForReagents by viewModel.photosForGels.collectAsState()


    var isFieldMenuExpanded by remember { mutableStateOf(false) }
    var isWellMenuExpanded by remember { mutableStateOf(false) }
    var isLayerMenuExpanded by remember { mutableStateOf(false) }
    var isCustomerMenuExpanded by remember { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }
    var selectedReagent by remember { mutableStateOf<String>("") }

    val photoSampling by viewModel.photoSampling.collectAsState()

    var selectedPhotoType by remember { mutableStateOf(PhotoType.PHOTO_SAMPLING) }

    val sharedPreferences =
        LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val employeeId = sharedPreferences.getInt("employeeId", 0)

    // Добавьте это состояние в ваш Composable
    var showReportNameDialog by remember { mutableStateOf(false) }
    var reportName by remember { mutableStateOf("") }

    val uniqueCode = remember {
        mutableStateOf(UUID.randomUUID().toString().take(8).uppercase())
    }

    LaunchedEffect(selectedCustomer, selectedField, selectedLayer, selectedWell) {
        if (selectedCustomer != null && selectedField != null &&
            selectedLayer != null && selectedWell != null
        ) {

            val safeCompanyName = transliterate(selectedCustomer!!.companyName)
                .replace(" ", "_")
                .replace("[^a-zA-Z0-9_]".toRegex(), "")

            val safeFieldName = transliterate(selectedField!!.name)
                .replace(" ", "_")
                .replace("[^a-zA-Z0-9_]".toRegex(), "")

            val safeLayerName = transliterate(selectedLayer!!.layerName)
                .replace(" ", "_")
                .replace("[^a-zA-Z0-9_]".toRegex(), "")

            val safeWellNumber = transliterate(selectedWell!!.wellNumber)
                .replace("[^a-zA-Z0-9_]".toRegex(), "")

            reportName = "Gel_${safeCompanyName}_${safeFieldName}_" +
                    "${safeLayerName}_${safeWellNumber}_" +
                    SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.US).format(Date())
        }
    }


    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                when (selectedPhotoType) {
                    PhotoType.PHOTO_SAMPLING -> viewModel.setPhotoSampling(uri)
                    PhotoType.PHOTO_REAGENT -> viewModel.addPhotoForReagent(selectedReagent, uri)
                    else -> {}
                }
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
                photoUriForCamera?.let { uri ->
                    when (selectedPhotoType) {
                        PhotoType.PHOTO_SAMPLING -> viewModel.setPhotoSampling(uri)
                        PhotoType.PHOTO_REAGENT -> viewModel.addPhotoForReagent(selectedReagent, uri)
                        else -> {}
                    }
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
                        text = "Гель",
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "№ ${uniqueCode.value}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            // Копируем код в буфер обмена
                            copyToClipboard(context, uniqueCode.value)
                            Toast.makeText(context, "Номер скопирован", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_content_copy_24),
                            contentDescription = "Копировать код"
                        )
                    }
                }
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
                FixingTestPhotoSection(
                    photo = photoSampling,
                    text = "Фотография отбора пробы (фиксация отбора на скважине)",
                    onAddPhoto = {
                        selectedPhotoType = PhotoType.PHOTO_SAMPLING
                        showPhotoSourceDialog = true
                    },
                    onRemovePhoto = {
                        viewModel.clearPhotoSampling()
                    },
                    modifier = Modifier.height(200.dp)
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
                GelPhotoSection(
                    gelName = "Фотография отбора пробы (линейный гель)",
                    photos = photosForReagents["Фотография отбора пробы (линейный гель)"] ?: emptyList(),
                    onAddPhoto = { reagent ->
                        selectedPhotoType = PhotoType.PHOTO_REAGENT
                        selectedReagent = reagent
                        showPhotoSourceDialog = true
                    },
                    onRemovePhoto = { photo ->
                        viewModel.removePhotoForReagent(
                            "Фотография отбора пробы (линейный гель)",
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
                GelPhotoSection(
                    gelName = "Фотография отбора пробы (сшитый гель)",
                    photos = photosForReagents["Фотография отбора пробы (сшитый гель)"] ?: emptyList(),
                    onAddPhoto = { reagent ->
                        selectedPhotoType = PhotoType.PHOTO_REAGENT
                        selectedReagent = reagent
                        showPhotoSourceDialog = true
                    },
                    onRemovePhoto = { photo ->
                        viewModel.removePhotoForReagent(
                            "Фотография отбора пробы (сшитый гель)",
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
                GelPhotoSection(
                    gelName = "Фотография отбора пробы (сшитый гель с пропантом)",
                    photos = photosForReagents["Фотография отбора пробы (сшитый гель с пропантом)"] ?: emptyList(),
                    onAddPhoto = { reagent ->
                        selectedPhotoType = PhotoType.PHOTO_REAGENT
                        selectedReagent = reagent
                        showPhotoSourceDialog = true
                    },
                    onRemovePhoto = { photo ->
                        viewModel.removePhotoForReagent(
                            "Фотография отбора пробы (сшитый гель с пропантом)",
                            photo
                        )
                    }
                )

            }

            item {


                Spacer(modifier = Modifier.height(32.dp))
            }

            item {

                Button(
                    onClick = {
                        if (selectedField != null && selectedWell != null && selectedLayer != null && selectedCustomer != null) {
                            showReportNameDialog = true
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

        LoadingAndSuccessDialog(
            isLoading = isLoading,
            isSuccess = isSuccess,
            onDismiss = { viewModel.resetSuccessState() }
        )


        if (showReportNameDialog) {

            ReportNameDialog(
                reportName = reportName,
                onDismissRequest = {
                    showReportNameDialog = false
                },
                onConfirm = {
                    if (selectedField != null && selectedWell != null && selectedLayer != null &&
                        selectedCustomer != null
                    ) {

                        viewModel.viewModelScope.launch {
                            val reportId = viewModel.saveReportAndGetId(
                                report = GelReport(
                                    employeeId = employeeId,
                                    fieldId = selectedField!!.id,
                                    wellId = selectedWell!!.id,
                                    layerId = selectedLayer!!.id,
                                    customerId = selectedCustomer!!.id,
                                    reportName = reportName,
                                    code = uniqueCode.value
                                ),
                                gelReportCode = uniqueCode.value,
                                photoSamplingUri = photoSampling!!,
                            )
                            viewModel.saveAllPhotosForReport(reportId!!, context)

                        }
                    }
                },
            )
        }

        if (showPhotoSourceDialog) {

            PhotoSourceDialog(
                onDismissRequest = {
                    showPhotoSourceDialog = false
                },
                onGallerySelected = {
                    galleryLauncher.launch("image/*")
                },
                onCameraSelected = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        launchCamera()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )

        }


    }
}

@Composable
fun FixingTestPhotoSection(
    photo: Uri?,
    onAddPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .dashedBorder(1.dp, MaterialTheme.colorScheme.primary, 8.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
        ) {
            // Отображение фотографии
            if (photo != null) {
                EmulsionPhotoCard(
                    photo = photo,
                    onRemovePhoto = onRemovePhoto,
                    modifier = modifier
                )
            } else {
                AddEmulsionPhotoCard(
                    onAddPhoto = onAddPhoto,
                    modifier = modifier
                )
            }

            // Текст под фотографией
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun GelPhotoSection(
    gelName: String,
    photos: List<Photo>,
    onAddPhoto: (String) -> Unit,
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
                    .heightIn(max = 1000.dp)
                    .animateContentSize()
            ) {
                items(photos) { photo ->
                    PhotoCard(
                        photo = photo,
                        onRemovePhoto = { onRemovePhoto(photo) }
                    )
                }
                item {
                    AddPhotoCard(
                        onAddPhoto = { onAddPhoto(gelName) } // Передаем имя реагента
                    )
                }
            }

            // Текст под фотографиями
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = gelName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )
        }
    }
}




@Preview
@Composable
fun TikTokScreen() {
    var selectedTab by remember { mutableStateOf(Tab.RECOMMENDATIONS) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновое изображение
        Image(
            painter = painterResource(id = R.drawable.baseline_insert_photo_24), // Замените на ваш ресурс
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Верхняя панель с табами и поиском
        TopBar(selectedTab, onTabSelected = { selectedTab = it })

        // Боковая панель с действиями
        ActionsColumn()
    }
}

@Composable
private fun TopBar(selectedTab: Tab, onTabSelected: (Tab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Табы рекомендации/подписки
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            TabButton(
                text = "Рекомендации",
                isSelected = selectedTab == Tab.RECOMMENDATIONS,
                onClick = { onTabSelected(Tab.RECOMMENDATIONS) }
            )

            TabButton(
                text = "Подписки",
                isSelected = selectedTab == Tab.FOLLOWING,
                onClick = { onTabSelected(Tab.FOLLOWING) }
            )
        }

        // Кнопка поиска
        IconButton(
            onClick = { /* Обработка поиска */ },
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color.White,
        fontSize = if (isSelected) 18.sp else 16.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun ActionsColumn() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        ActionButton(
            iconRes = R.drawable.baseline_filter_list_alt_24,
            count = "125K"
        )
        ActionButton(iconRes = R.drawable.baseline_filter_list_alt_24, count = "1.2K")
        ActionButton(iconRes = R.drawable.baseline_filter_list_alt_24, count = "8.5K")
        ActionButton(iconRes = R.drawable.baseline_filter_list_alt_24, count = "542")
    }
}

@Composable
private fun ActionButton(iconRes: Int, count: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = count,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private enum class Tab {
    RECOMMENDATIONS, FOLLOWING
}


