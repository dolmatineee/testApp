package com.example.testapp.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.testapp.R
import com.example.testapp.domain.models.BaseReport
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.ReportPhoto
import com.example.testapp.domain.models.Well
import com.example.testapp.ui.viewmodels.SupervisorReportsViewModel
import com.example.testapp.utils.formatDateTime
import com.example.testapp.utils.toImageBitmap
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SupervisorReportsScreen(
    viewModel: SupervisorReportsViewModel = hiltViewModel()
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
            viewModel.uploadSupervisorSignature(
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


@Composable
fun ErrorMessage(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onRetry) {
                Text("Повторить")
            }

            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    }
}


/*@Composable
fun ReportsList(
    reports: List<BlenderReport>,
    onReportClick: (BlenderReport) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(reports) { report ->
            ReportCard(
                report = report,
                onClick = { onReportClick(report) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}*/
@Composable
fun ReportCardSignature(
    report: BaseReport,
    onClick: () -> Unit,
    photos: List<ReportPhoto> = emptyList(),
    modifier: Modifier = Modifier,
    field: Flow<Field?>,
    well: Flow<Well?>,
    layer: Flow<Layer?>,
    customer: Flow<Customer?>
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedPhoto by remember { mutableStateOf<String?>(null) }

    // Диалог для просмотра фотографии
    if (selectedPhoto != null) {
        Dialog(
            onDismissRequest = { selectedPhoto = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                AsyncImage(
                    model = selectedPhoto,
                    contentDescription = "Увеличенное фото",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                )


                // Кнопка закрытия
                IconButton(
                    onClick = { selectedPhoto = null },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(0.6f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = Color.White
                    )
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Основная информация
            Text(
                text = report.reportName,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Код: ${report.code}",
                style = MaterialTheme.typography.labelLarge,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Создан: ${report.createdAt?.let { formatDateTime(it) }}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
            )

            /*if (report.supervisorSignatureUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                BadgedBox(
                    badge = {
                        Badge(containerColor = MaterialTheme.colorScheme.inverseOnSurface) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Подписано",
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                ) {
                    Text(
                        text = "Подписано супервайзером",
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }
            }
*/
            Spacer(modifier = Modifier.height(12.dp))
            Divider()

            // Раскрывающаяся часть
            AnimatedVisibility(visible = expanded) {
                Column {
                    // Показываем дополнительные поля
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Детали отчета:", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Месторождение: ${field.collectAsState(null).value?.name}",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "Скважина: ${well.collectAsState(null).value?.wellNumber}",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "Пласт: ${layer.collectAsState(null).value?.layerName}",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "Заказчик: ${customer.collectAsState(null).value?.companyName}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Показываем фотографии
                    if (photos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow {
                            items(photos) { photo ->
                                AsyncImage(
                                    contentScale = ContentScale.Crop,
                                    model = photo.photoUrl,
                                    contentDescription = "Фото отчета",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedPhoto = photo.photoUrl }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))


                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            onClick()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 4.dp),
                            text = "Подписать отчет"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                }


            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .dashedBorder(1.dp, MaterialTheme.colorScheme.onSurface, 8.dp)
                    .clickable { expanded = !expanded }
                    .background(shape = RoundedCornerShape(8.dp), color = Color.Transparent)
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                    text = if (expanded) "Свернуть" else "Подробнее",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun ReagentsTable(reagents: List<Reagent>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Заголовок таблицы
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Реагент",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Расход",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Конц.",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Время",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Кол-во",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        reagents.flatMap { reagent ->
            reagent.tests.map { test -> reagent to test }
        }.forEach { (reagent, test) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    reagent.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    test.flowRate.toString(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    test.concentration.toString(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    test.testTime.toString(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    test.actualAmount.toString(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Divider()
        }
    }
}




/*
data class Comment(
    val id: Int,
    val username: String,
    val avatarRes: Int,
    val text: String,
    val date: String,
    var likesCount: Int,
    var isLiked: Boolean = false
)

@Preview
@Composable
fun CommentsScreen() {
    // Пример данных комментариев
    var comments by remember {
        mutableStateOf(
            listOf(
                Comment(
                    id = 1,
                    username = "user123",
                    avatarRes = R.drawable.background,
                    text = "Это отличный пост, спасибо за информацию!",
                    date = "2 часа назад",
                    likesCount = 15
                ),
                Comment(
                    id = 2,
                    username = "developer42",
                    avatarRes = R.drawable.background,
                    text = "Интересная точка зрения, я бы добавил ещё несколько деталей.",
                    date = "5 часов назад",
                    likesCount = 8
                ),
                Comment(
                    id = 3,
                    username = "design_lover",
                    avatarRes = R.drawable.background,
                    text = "Очень красивое оформление!",
                    date = "Вчера",
                    likesCount = 23
                )
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        items(comments) { comment ->
            CommentItem(
                comment = comment,
                onLikeClick = {
                    comments = comments.map {
                        if (it.id == comment.id) {
                            it.copy(
                                isLiked = !it.isLiked,
                                likesCount = if (!it.isLiked) it.likesCount + 1 else it.likesCount - 1
                            )
                        } else {
                            it
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun CommentItem(comment: Comment, onLikeClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(12.dp)
    ) {
        // Верхняя часть с аватаркой и именем пользователя
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = comment.avatarRes),
                contentDescription = "Аватар пользователя ${comment.username}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = comment.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = comment.date,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        // Текст комментария
        Text(
            text = comment.text,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            fontSize = 14.sp
        )

        // Кнопка лайка
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.clickable { onLikeClick() }
        ) {
            Icon(
                imageVector = if (comment.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Лайк",
                tint = if (comment.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = comment.likesCount.toString(),
                fontSize = 14.sp,
                color = if (comment.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
*/


