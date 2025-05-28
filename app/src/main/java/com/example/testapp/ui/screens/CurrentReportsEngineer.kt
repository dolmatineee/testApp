package com.example.testapp.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.testapp.domain.models.BaseReport
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.ReportPhoto
import com.example.testapp.domain.models.Well
import com.example.testapp.ui.viewmodels.EngineerReportsViewModel
import com.example.testapp.ui.viewmodels.SupervisorReportsViewModel
import com.example.testapp.utils.formatDateTime
import com.example.testapp.utils.toImageBitmap
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EngineerReportsScreen(
    viewModel: EngineerReportsViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val signatureBase64 = viewModel.getSignature()
    val signatureBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

    val context = LocalContext.current

    val filename = "signature_${System.currentTimeMillis()}.png"
    val file = File(context.filesDir, filename)

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var currentReport: BaseReport? by remember { mutableStateOf(null) }

    fun onSignReportClicked(report: BaseReport) {
        currentReport = report
        showConfirmationDialog = true
    }

    fun confirmSignReport() {
        showConfirmationDialog = false
        showLoadingDialog = true

        currentReport.let { report ->
            viewModel.uploadEngineerSignature(
                reportId = report?.id!!,
                signatureFile = file,
                reportType = report.reportType
            ) { success ->


                showLoadingDialog = false
                if (success) {
                    showSuccessDialog = true
                } else {
                    Toast.makeText(
                        context,
                        "Произошла ошибка, повторите попытку",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(signatureBase64) {
        if (signatureBase64 != null) {
            signatureBitmap.value = signatureBase64.toImageBitmap()
            val bitmap = signatureBitmap.value!!.asAndroidBitmap()


            // Сохраняем Bitmap в PNG
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // 100 - максимальное качество
            }

        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Отчеты на подпись",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(6) {

                    ShimmerLoadingItem(180.dp, 12.dp)

                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports) { report ->
                    val photos by viewModel.getReportPhotos(report.id!!, report.reportType)
                        .collectAsState(initial = emptyList())
                    Log.e("fghfghfghh", report.toString())

                    ReportCardSignature(
                        photos = photos,
                        report = report,
                        onClick = {
                            onSignReportClicked(report)
                        },
                        field = viewModel.getFieldById(report.fieldId!!),
                        well = viewModel.getWellById(report.wellId!!),
                        layer = viewModel.getLayerById(report.layerId!!),
                        customer = viewModel.getCustomerById(report.customerId!!),
                    )

                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        if (showConfirmationDialog) {

            Dialog(
                onDismissRequest = {
                    showConfirmationDialog = false
                },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = "Подтверждение",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Вы уверены, что хотите подписать отчет?",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button (
                            modifier = Modifier.weight(1f),
                            onClick = {
                                showConfirmationDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("Отмена")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                confirmSignReport()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Подтвердить")
                        }
                    }
                }
            }

        }


        if (showLoadingDialog) {
            Dialog(
                onDismissRequest = { /* Нельзя закрыть */ },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Подписание отчета...")
                    }
                }
            }
        }

        // Диалог успеха
        if (showSuccessDialog) {

            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.Asset("anim_check.json")
            )
            Dialog(
                onDismissRequest = { showSuccessDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Column(
                    modifier = Modifier
                        .size(250.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(150.dp),
                        speed = 1f
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            showSuccessDialog = false
                            viewModel.loadReports()
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 4.dp),
                            text = "Закрыть",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }


    }
}

