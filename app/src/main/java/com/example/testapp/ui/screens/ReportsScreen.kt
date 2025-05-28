package com.example.testapp.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.R
import com.example.testapp.domain.models.BaseReport
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.models.ReportStatus
import com.example.testapp.ui.viewmodels.ReportsScreenViewModel
import kotlinx.coroutines.flow.Flow

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsScreenViewModel = hiltViewModel(),
    onFilterClickListener: () -> Unit,
    reportFilters: ReportFilters
) {

    val reports by viewModel.reports.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    val statuses by viewModel.statuses.collectAsState()

    LaunchedEffect(reportFilters) {
        viewModel.loadReports(reportFilters)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterStart),
                            text = "История",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        IconButton(
                            onClick = {
                                onFilterClickListener()
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_filter_list_alt_24),
                                contentDescription = "Filter",
                            )
                        }
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
        when {
            isLoading -> {
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
            }


            reports.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 12.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reports) { report ->
                        Log.e("report", report.fileUrl.toString())
                        ReportItem(
                            report = report,
                            reportStatus = report.status
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Отчеты не найдены",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


@Composable
fun ReportItem(
    report: BaseReport,
    reportStatus: String?
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Разрешаем раскрывать только завершенные отчеты
    val canExpand = reportStatus == "Завершен"

    // Функция для скачивания файла
    fun downloadFile() {
        report.fileUrl?.let { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка при открытии файла", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "Файл недоступен", Toast.LENGTH_SHORT).show()
        }
    }

    // Функция для шаринга файла
    fun shareFile() {
        report.fileUrl?.let { url ->
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, url)
                context.startActivity(Intent.createChooser(shareIntent, "Поделиться отчетом"))
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка при попытке поделиться", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "Файл недоступен", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = report.reportName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (reportStatus != null) {
                        Text(
                            text = reportStatus,
                            style = MaterialTheme.typography.labelMedium,
                            color = when(reportStatus) {
                                "Ожидается подпись супервайзера" -> MaterialTheme.colorScheme.error
                                "Ожидается подпись инженера" -> MaterialTheme.colorScheme.errorContainer
                                "Завершен" -> MaterialTheme.colorScheme.onError
                                else -> {MaterialTheme.colorScheme.onBackground.copy(0.5f)}
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = report.createdAt!!.substring(0, 10),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                    )
                }

                if ( canExpand) {
                    IconButton(
                        onClick = { isExpanded = canExpand && !isExpanded },
                        enabled = canExpand,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor =  Color.Black.copy(0.7f) ,
                            contentColor =  Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
                        )
                    }
                }


            }

            if (isExpanded && canExpand) {
                Spacer(modifier = Modifier.height(12.dp))

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(0.1f))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButtonForReport(
                        painter = painterResource(R.drawable.outline_file_download_24),
                        text = "Скачать",
                        onClick = { downloadFile() }
                    )

                    ActionButtonForReport(
                        painter = painterResource(R.drawable.baseline_share_24),
                        text = "Поделиться",
                        onClick = { shareFile() }
                    )
                }
            }
        }
    }
}
@Composable
fun ActionButtonForReport(
    painter: Painter,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            }
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
        )
    }
}