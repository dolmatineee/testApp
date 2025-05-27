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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.example.testapp.domain.models.AcidReport
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.ui.customs.CustomDropdownMenu
import com.example.testapp.ui.customs.CustomTextField
import com.example.testapp.ui.viewmodels.AcidScreenViewModel
import com.example.testapp.utils.PhotoType
import com.example.testapp.utils.copyToClipboard
import com.example.testapp.utils.generateAcidReport
import com.example.testapp.utils.toImageBitmap
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
fun AcidScreen(
    viewModel: AcidScreenViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onSignatureCardClickListener: () -> Unit
) {
    val fields by viewModel.fields.collectAsState()
    val wells by viewModel.wells.collectAsState()
    val layers by viewModel.layers.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val laboratorians by viewModel.laboratorians.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    val selectedField by viewModel.selectedField.collectAsState()
    val selectedWell by viewModel.selectedWell.collectAsState()
    val selectedLayer by viewModel.selectedLayer.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val selectedLaboratorian by viewModel.selectedLaboratorian.collectAsState()

    var preparedAcid by remember { mutableStateOf("0") }
    var concentratedAcid by remember { mutableStateOf("0") }


    var isFieldMenuExpanded by remember { mutableStateOf(false) }
    var isWellMenuExpanded by remember { mutableStateOf(false) }
    var isLayerMenuExpanded by remember { mutableStateOf(false) }
    var isCustomerMenuExpanded by remember { mutableStateOf(false) }
    var isLaboratorianMenuExpanded by remember { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }


    val photo5000General by viewModel.photo5000General.collectAsState()
    val photo5000AfterPour_25_75 by viewModel.photo5000AfterPour_25_75.collectAsState()
    val photo5000AfterPour_50_50 by viewModel.photo5000AfterPour_50_50.collectAsState()
    val photo5000AfterPour_75_25 by viewModel.photo5000AfterPour_75_25.collectAsState()
    val photo5000AfterPour_spent by viewModel.photo5000AfterPour_spent.collectAsState()

    val photo2000General by viewModel.photo2000General.collectAsState()
    val photo2000AfterPour_25_75 by viewModel.photo2000AfterPour_25_75.collectAsState()
    val photo2000AfterPour_50_50 by viewModel.photo2000AfterPour_50_50.collectAsState()
    val photo2000AfterPour_75_25 by viewModel.photo2000AfterPour_75_25.collectAsState()
    val photo2000AfterPour_spent by viewModel.photo2000AfterPour_spent.collectAsState()

    val photoDensimeterConcentratedAcid by viewModel.photoDensimeterConcentratedAcid.collectAsState()
    val photoDensimeterPreparedAcid by viewModel.photoDensimeterPreparedAcid.collectAsState()

    var photoUriForCamera by remember { mutableStateOf<Uri?>(null) }
    var selectedPhotoType by remember { mutableStateOf<PhotoType>(PhotoType.PHOTO_5000_GENERAL) }
    val signatureBase64 = viewModel.getSignature()
    val signatureBitmap = remember { mutableStateOf<ImageBitmap?>(null) }


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

            reportName = "Acid_${safeCompanyName}_${safeFieldName}_" +
                    "${safeLayerName}_${safeWellNumber}_" +
                    SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.US).format(Date())
        }
    }

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
                when (selectedPhotoType) {
                    PhotoType.PHOTO_5000_GENERAL -> viewModel.setPhoto5000General(uri)
                    PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> viewModel.setPhoto5000AfterPour_25_75(
                        uri
                    )

                    PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> viewModel.setPhoto5000AfterPour_50_50(
                        uri
                    )

                    PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> viewModel.setPhoto5000AfterPour_75_25(
                        uri
                    )

                    PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> viewModel.setPhoto5000AfterPour_spent(
                        uri
                    )

                    PhotoType.PHOTO_2000_GENERAL -> viewModel.setPhoto2000General(uri)
                    PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> viewModel.setPhoto2000AfterPour_25_75(
                        uri
                    )

                    PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> viewModel.setPhoto2000AfterPour_50_50(
                        uri
                    )

                    PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> viewModel.setPhoto2000AfterPour_75_25(
                        uri
                    )

                    PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> viewModel.setPhoto2000AfterPour_spent(
                        uri
                    )

                    PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> viewModel.setPhotoDensimeterPreparedAcid(
                        uri
                    )

                    PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> viewModel.setPhotoDensimeterConcentratedAcid(
                        uri
                    )

                    PhotoType.PHOTO_SAMPLING -> {}
                    PhotoType.PHOTO_REAGENT -> {}
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


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                photoUriForCamera?.let { uri ->
                    when (selectedPhotoType) {
                        PhotoType.PHOTO_5000_GENERAL -> viewModel.setPhoto5000General(uri)
                        PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> viewModel.setPhoto5000AfterPour_25_75(
                            uri
                        )

                        PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> viewModel.setPhoto5000AfterPour_50_50(
                            uri
                        )

                        PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> viewModel.setPhoto5000AfterPour_75_25(
                            uri
                        )

                        PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> viewModel.setPhoto5000AfterPour_spent(
                            uri
                        )

                        PhotoType.PHOTO_2000_GENERAL -> viewModel.setPhoto2000General(uri)
                        PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> viewModel.setPhoto2000AfterPour_25_75(
                            uri
                        )

                        PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> viewModel.setPhoto2000AfterPour_50_50(
                            uri
                        )

                        PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> viewModel.setPhoto2000AfterPour_75_25(
                            uri
                        )

                        PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> viewModel.setPhoto2000AfterPour_spent(
                            uri
                        )

                        PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> viewModel.setPhotoDensimeterPreparedAcid(
                            uri
                        )

                        PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> viewModel.setPhotoDensimeterConcentratedAcid(
                            uri
                        )

                        PhotoType.PHOTO_SAMPLING -> {}
                        PhotoType.PHOTO_REAGENT -> {}
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
                    text = "Лаборант",
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
                    label = "Лаборант",
                    items = laboratorians.map { it.fullName },
                    selectedItem = selectedLaboratorian?.fullName,
                    isExpanded = isLaboratorianMenuExpanded,
                    onExpandedChange = { isLaboratorianMenuExpanded = it },
                    onItemSelected = { fullName ->
                        viewModel.onLaboratorianSelected(laboratorians.first { it.fullName == fullName })
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomTextField(
                        value = concentratedAcid,
                        onValueChange = { concentratedAcid = it },
                        label = "Концентрированная %",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    CustomTextField(
                        value = preparedAcid,
                        onValueChange = { preparedAcid = it },
                        label = "Приготовленная %",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                EmulsionTestPhotoRowAcid(
                    photoDensimeterPreparedAcid = photoDensimeterPreparedAcid,
                    photoDensimeterConcentratedAcid = photoDensimeterConcentratedAcid,
                    onAddPhoto = { photoType ->
                        when (photoType) {
                            PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> TODO()
                            PhotoType.PHOTO_5000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> TODO()

                            PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> TODO()

                            PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> TODO()

                            PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> TODO()

                            PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> {
                                selectedPhotoType = PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> {
                                selectedPhotoType = PhotoType.PHOTO_DENSIMETER_PREPARED_ACID
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_SAMPLING -> TODO()
                            PhotoType.PHOTO_REAGENT -> {}
                        }
                    },
                    onRemovePhoto = { photoType ->
                        when (photoType) {
                            PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> TODO()
                            PhotoType.PHOTO_5000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> TODO()
                            PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> viewModel.clearPhotoDensimeterConcentratedAcid()
                            PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> viewModel.clearPhotoDensimeterPreparedAcid()
                            PhotoType.PHOTO_SAMPLING -> TODO()
                            PhotoType.PHOTO_REAGENT -> {}
                        }
                    }
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
                    photo = photo5000General,
                    text = "Фотография теста на совместимость и распад эмульсии 5000 мг/л",
                    onAddPhoto = {
                        selectedPhotoType = PhotoType.PHOTO_5000_GENERAL
                        showPhotoSourceDialog = true
                    },
                    onRemovePhoto = {
                        viewModel.clearPhoto5000General()
                    },
                    modifier = Modifier.height(200.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                EmulsionTestPhotoGrid5000(
                    photo5000AfterPour_25_75 = photo5000AfterPour_25_75,
                    photo5000AfterPour_50_50 = photo5000AfterPour_50_50,
                    photo5000AfterPour_75_25 = photo5000AfterPour_75_25,
                    photo5000AfterPour_spent = photo5000AfterPour_spent,
                    onAddPhoto = { photoType ->
                        when (photoType) {
                            PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> {
                                selectedPhotoType = PhotoType.PHOTO_5000_AFTER_POUR_25_75
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> {
                                selectedPhotoType = PhotoType.PHOTO_5000_AFTER_POUR_50_50
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> {
                                selectedPhotoType = PhotoType.PHOTO_5000_AFTER_POUR_75_25
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> {
                                selectedPhotoType = PhotoType.PHOTO_5000_AFTER_POUR_SPENT
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_5000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> TODO()
                            PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> TODO()
                            PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> TODO()
                            PhotoType.PHOTO_SAMPLING -> TODO()
                            PhotoType.PHOTO_REAGENT -> {}
                        }
                    },
                    onRemovePhoto = { photoType ->
                        when (photoType) {
                            PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> viewModel.clearPhoto5000AfterPour_25_75()
                            PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> viewModel.clearPhoto5000AfterPour_50_50()
                            PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> viewModel.clearPhoto5000AfterPour_75_25()
                            PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> viewModel.clearPhoto5000AfterPour_spent()
                            PhotoType.PHOTO_5000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> TODO()
                            PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> TODO()
                            PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> TODO()
                            PhotoType.PHOTO_SAMPLING -> TODO()
                            PhotoType.PHOTO_REAGENT -> {}
                        }
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
                EmulsionTestPhotoSection(
                    photo = photo2000General,
                    text = "Фотография теста на совместимость и распад эмульсии 2000 мг/л",
                    onAddPhoto = {
                        selectedPhotoType = PhotoType.PHOTO_2000_GENERAL
                        showPhotoSourceDialog = true
                    },
                    onRemovePhoto = {
                        viewModel.clearPhoto2000General()
                    },
                    modifier = Modifier.height(200.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                EmulsionTestPhotoGrid2000(
                    photo2000AfterPour_25_75 = photo2000AfterPour_25_75,
                    photo2000AfterPour_50_50 = photo2000AfterPour_50_50,
                    photo2000AfterPour_75_25 = photo2000AfterPour_75_25,
                    photo2000AfterPour_spent = photo2000AfterPour_spent,
                    onAddPhoto = { photoType ->
                        when (photoType) {
                            PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> TODO()
                            PhotoType.PHOTO_5000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> {
                                selectedPhotoType = PhotoType.PHOTO_2000_AFTER_POUR_25_75
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> {
                                selectedPhotoType = PhotoType.PHOTO_2000_AFTER_POUR_50_50
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> {
                                selectedPhotoType = PhotoType.PHOTO_2000_AFTER_POUR_75_25
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> {
                                selectedPhotoType = PhotoType.PHOTO_2000_AFTER_POUR_SPENT
                                showPhotoSourceDialog = true
                            }

                            PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> TODO()
                            PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> TODO()
                            PhotoType.PHOTO_SAMPLING -> TODO()
                            PhotoType.PHOTO_REAGENT -> {}
                        }
                    },
                    onRemovePhoto = { photoType ->
                        when (photoType) {
                            PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> TODO()
                            PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> TODO()
                            PhotoType.PHOTO_5000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_GENERAL -> TODO()
                            PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> viewModel.clearPhoto2000AfterPour_25_75()
                            PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> viewModel.clearPhoto2000AfterPour_50_50()
                            PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> viewModel.clearPhoto2000AfterPour_75_25()
                            PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> viewModel.clearPhoto2000AfterPour_spent()
                            PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> TODO()
                            PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> TODO()
                            PhotoType.PHOTO_SAMPLING -> TODO()
                            PhotoType.PHOTO_REAGENT -> {}
                        }
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
                            /*val reportFile = generateAcidReport(
                                customer = selectedCustomer!!,
                                field = selectedField!!,
                                layer = selectedLayer!!,
                                well = selectedWell!!,
                                signatureBitmap = signatureBitmap.value!!,
                                context = context,
                                photo5000General = photo5000General!!,
                                photo5000AfterPour_25_75 = photo5000AfterPour_25_75!!,
                                photo5000AfterPour_50_50 = photo5000AfterPour_50_50!!,
                                photo5000AfterPour_75_25 = photo5000AfterPour_75_25!!,
                                photo5000AfterPour_spent = photo5000AfterPour_spent!!,
                                photo2000General = photo2000General!!,
                                photo2000AfterPour_25_75 = photo2000AfterPour_25_75!!,
                                photo2000AfterPour_50_50 = photo2000AfterPour_50_50!!,
                                photo2000AfterPour_75_25 = photo2000AfterPour_75_25!!,
                                photo2000AfterPour_spent = photo2000AfterPour_spent!!
                            )*/

                            showReportNameDialog = true

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
                                .aspectRatio(1f),
                            colors = CardDefaults.cardColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.primary.copy(0.1f)
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
                                .aspectRatio(1f),
                            colors = CardDefaults.cardColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.primary.copy(0.1f)
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
                            val report = AcidReport(
                                employeeId = employeeId,
                                fieldId = selectedField!!.id,
                                wellId = selectedWell!!.id,
                                layerId = selectedLayer!!.id,
                                customerId = selectedCustomer!!.id,
                                preparedAcidPercentage = preparedAcid.toDoubleOrNull() ?: 0.0,
                                concentratedAcidPercentage = concentratedAcid.toDoubleOrNull()
                                    ?: 0.0,
                                labTechnicianId = selectedLaboratorian!!.id,
                                code = uniqueCode.value,
                                reportName = reportName,
                            )

                            val reportId = viewModel.saveReportAndGetId(
                                report = report,
                                acidReportCode = uniqueCode.value,
                                context = context
                            )


                            if (reportId != null) {
                                viewModel.setSuccessState()
                            }
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
fun EmulsionTestPhotoSection(
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
fun EmulsionTestPhotoRowAcid(
    photoDensimeterPreparedAcid: Uri?,
    photoDensimeterConcentratedAcid: Uri?,
    onAddPhoto: (PhotoType) -> Unit,
    onRemovePhoto: (PhotoType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .heightIn(max = 1000.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(2) { index ->
            when (index) {
                0 -> EmulsionTestPhotoSection(
                    photo = photoDensimeterConcentratedAcid,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID) },
                    text = "Фотография плотномера при замере плотности концентрированной кислоты",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                1 -> EmulsionTestPhotoSection(
                    photo = photoDensimeterPreparedAcid,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_DENSIMETER_PREPARED_ACID) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_DENSIMETER_PREPARED_ACID) },
                    text = "Фотография плотномера при замере плотности приготовленной кислоты",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }
    }
}


@Composable
fun EmulsionTestPhotoGrid5000(
    photo5000AfterPour_25_75: Uri?,
    photo5000AfterPour_50_50: Uri?,
    photo5000AfterPour_75_25: Uri?,
    photo5000AfterPour_spent: Uri?,
    onAddPhoto: (PhotoType) -> Unit,
    onRemovePhoto: (PhotoType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .heightIn(max = 1000.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(4) { index ->
            when (index) {
                0 -> EmulsionTestPhotoSection(
                    photo = photo5000AfterPour_25_75,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_5000_AFTER_POUR_25_75) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_5000_AFTER_POUR_25_75) },
                    text = "Пролив 25/75",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                1 -> EmulsionTestPhotoSection(
                    photo = photo5000AfterPour_50_50,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_5000_AFTER_POUR_50_50) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_5000_AFTER_POUR_50_50) },
                    text = "Пролив 50/50",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                2 -> EmulsionTestPhotoSection(
                    photo = photo5000AfterPour_75_25,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_5000_AFTER_POUR_75_25) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_5000_AFTER_POUR_75_25) },
                    text = "Пролив 75/25",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                3 -> EmulsionTestPhotoSection(
                    photo = photo5000AfterPour_spent,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_5000_AFTER_POUR_SPENT) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_5000_AFTER_POUR_SPENT) },
                    text = "Пролив отработанный",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }
    }
}


@Composable
fun EmulsionTestPhotoGrid2000(
    photo2000AfterPour_25_75: Uri?,
    photo2000AfterPour_50_50: Uri?,
    photo2000AfterPour_75_25: Uri?,
    photo2000AfterPour_spent: Uri?,
    onAddPhoto: (PhotoType) -> Unit,
    onRemovePhoto: (PhotoType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 колонки
        modifier = modifier
            .fillMaxSize()
            .heightIn(max = 1000.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(4) { index ->
            when (index) {
                0 -> EmulsionTestPhotoSection(
                    photo = photo2000AfterPour_25_75,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_2000_AFTER_POUR_25_75) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_2000_AFTER_POUR_25_75) },
                    text = "Пролив 25/75",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                1 -> EmulsionTestPhotoSection(
                    photo = photo2000AfterPour_50_50,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_2000_AFTER_POUR_50_50) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_2000_AFTER_POUR_50_50) },
                    text = "Пролив 50/50",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                2 -> EmulsionTestPhotoSection(
                    photo = photo2000AfterPour_75_25,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_2000_AFTER_POUR_75_25) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_2000_AFTER_POUR_75_25) },
                    text = "Пролив 75/25",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                3 -> EmulsionTestPhotoSection(
                    photo = photo2000AfterPour_spent,
                    onAddPhoto = { onAddPhoto(PhotoType.PHOTO_2000_AFTER_POUR_SPENT) },
                    onRemovePhoto = { onRemovePhoto(PhotoType.PHOTO_2000_AFTER_POUR_SPENT) },
                    text = "Пролив отработанный",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
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

