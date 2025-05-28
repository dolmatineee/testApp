package com.example.testapp.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.R
import com.example.testapp.domain.models.ChartData
import com.example.testapp.ui.viewmodels.AdministratorStatsScreenViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdministratorStatisticsScreen(
    viewModel: AdministratorStatsScreenViewModel = hiltViewModel(),
) {

    val reportsStatistics by viewModel.reportsStatistics.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val chartData = viewModel.chartData.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Статистика",
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalAlignment = CenterHorizontally
        ) {

            item {
                DateNavigation(
                    currentDate,
                    onPrevious = { viewModel.previousDate() },
                    onNext = { viewModel.nextDate() })
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                BarGraph(chartData = chartData.value)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }



            item {
                ReportsStatisticsCards(
                    blenderCount = reportsStatistics.blenderCount,
                    acidCount = reportsStatistics.acidCount,
                    gelCount = reportsStatistics.gelCount,
                    totalCount = reportsStatistics.total,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }

        }


    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateNavigation(currentDate: LocalDate, onPrevious: () -> Unit, onNext: () -> Unit) {
    val today = LocalDate.now()
    val isToday = currentDate.isEqual(today)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onPrevious,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_keyboard_arrow_left_24),
                contentDescription = "Previous"
            )
        }

        Text(
            text = currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )


        IconButton(
            onClick = onNext,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (!isToday) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
                contentColor = if (!isToday) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.background
            ),
            enabled = !isToday
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
                contentDescription = "Next"
            )
        }


    }
}

@Composable
fun ReportTypeCard(
    count: Int,
    title: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            // Круг с иконкой
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                        )
                    ) {
                        append("$count")
                    }
                },
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Текст
            Text(
                text = "Отчетов",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Текст
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ReportsStatisticsCards(
    blenderCount: Int,
    acidCount: Int,
    gelCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Первая строка с карточками блендера и кислоты
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ReportTypeCard(
                modifier = Modifier.weight(1f),
                count = blenderCount,
                title = "Блендер",
                icon = painterResource(R.drawable.baseline_list_alt_24),
            )

            Spacer(modifier = Modifier.width(16.dp))

            ReportTypeCard(
                modifier = Modifier.weight(1f),
                count = acidCount,
                title = "Кислота",
                icon = painterResource(R.drawable.baseline_list_alt_24),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Вторая строка с карточками геля и общего количества
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ReportTypeCard(
                modifier = Modifier.weight(1f),
                count = gelCount,
                title = "Гель",
                icon = painterResource(R.drawable.baseline_list_alt_24),
            )

            Spacer(modifier = Modifier.width(16.dp))

            ReportTypeCard(
                modifier = Modifier.weight(1f),
                count = totalCount,
                title = "Всего",
                icon = painterResource(R.drawable.baseline_list_alt_24),
            )
        }
    }
}


enum class BarType {
    CIRCULAR_TYPE,
    TOP_CURVED
}
@Composable
fun BarGraph(
    chartData: ChartData,
    height: Dp = 200.dp,
    roundType: BarType = BarType.TOP_CURVED,
    barWidth: Dp = 8.dp,
    barColor: Color = MaterialTheme.colorScheme.primary,
    barArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var typeface = Typeface.create("montserrat", Typeface.NORMAL)
    val sessionsByHour = chartData.sessionsByHour
    val yAxisLabels = chartData.yAxisLabels
    val xAxisLabels = chartData.xAxisLabels

    // Преобразуем данные для графика, учитывая все 24 часа
    val graphBarData = xAxisLabels.map { hour ->
        sessionsByHour[hour]?.toFloat() ?: 0f
    }

    // Часы, которые будут отображаться на оси X
    val visibleHours = listOf(0, 4, 8, 12, 16, 20, 24)

    // Остальной код остается практически без изменений, за исключением использования graphBarData и xAxisLabels

    val barData by remember {
        mutableStateOf(graphBarData.map { it.toInt() })
    }

    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp

    val xAxisScaleHeight = 40.dp

    val yAxisScaleSpacing by remember {
        mutableStateOf(100f)
    }
    val yAxisTextWidth by remember {
        mutableStateOf(100.dp)
    }

    val barShap = when (roundType) {
        BarType.CIRCULAR_TYPE -> CircleShape
        BarType.TOP_CURVED -> RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
    }
    // Применяем шрифт из стиля
    val context = LocalContext.current
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            // Устанавливаем цвет текста
            color = colorScheme.onSecondaryContainer.toArgb()

            // Выравнивание текста по центру
            textAlign = Paint.Align.CENTER

            // Получаем стиль текста из темы
            val textStyle = typography.labelMedium

            // Применяем размер текста из стиля
            textSize = with(density) { textStyle.fontSize.toPx() }


            typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular)

            letterSpacing = textStyle.letterSpacing.value


        }
    }

    val yCoordinates = mutableListOf<Float>()
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {

        Column(
            modifier = Modifier
                .padding(top = xAxisScaleHeight, end = 3.dp)
                .height(height)
                .fillMaxWidth(),
            horizontalAlignment = CenterHorizontally
        ) {

            Canvas(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxSize()
            ) {

                // Фиксированные значения для оси Y
                val yAxisScaleText = yAxisLabels
                val maxYValue = yAxisScaleText.maxOrNull() ?: 0

                // Отрисовка текста и линий для оси Y
                yAxisScaleText.forEachIndexed { i, value ->
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            value.toString(), // Целое число
                            30f,
                            size.height - yAxisScaleSpacing - i * size.height / (yAxisScaleText.size - 1),
                            textPaint
                        )
                    }
                    yCoordinates.add(size.height - yAxisScaleSpacing - i * size.height / (yAxisScaleText.size - 1))
                }

                // Отрисовка горизонтальных линий для оси Y
                yAxisScaleText.forEachIndexed { i, _ ->
                    drawLine(
                        start = Offset(x = yAxisScaleSpacing, y = yCoordinates[i]),
                        end = Offset(x = size.width, y = yCoordinates[i]),
                        color = colorScheme.surface,
                        strokeWidth = 5f,
                        pathEffect = pathEffect
                    )
                }

            }

        }

        Box(
            modifier = Modifier
                .padding(start = 50.dp)
                .width(width - yAxisTextWidth)
                .height(height + xAxisScaleHeight),
            contentAlignment = BottomCenter
        ) {

            Row(
                modifier = Modifier
                    .width(width - yAxisTextWidth),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = barArrangement
            ) {

                graphBarData.forEachIndexed { index, value ->

                    var animationTriggered by remember {
                        mutableStateOf(false)
                    }
                    val graphBarHeight by animateFloatAsState(
                        targetValue = if (animationTriggered) value else 0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = 0
                        )
                    )
                    LaunchedEffect(key1 = true) {
                        animationTriggered = true
                    }

                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .clip(barShap)
                                .width(barWidth)
                                .height(height - 10.dp)
                                .background(Color.Transparent),
                            contentAlignment = BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(barShap)
                                    .fillMaxWidth()
                                    .fillMaxHeight(graphBarHeight / (yAxisLabels.maxOrNull() ?: 1))
                                    .background(barColor)
                            )
                        }

                        // Отображаем текст только для выбранных часов
                        if (xAxisLabels[index] in visibleHours) {
                            Column(
                                modifier = Modifier
                                    .height(xAxisScaleHeight),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = CenterHorizontally
                            ) {


                                Text(
                                    modifier = Modifier.padding(bottom = 3.dp, top = 8.dp),
                                    text = xAxisLabels[index].toString(),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    style = MaterialTheme.typography.labelMedium
                                )

                            }
                        }

                    }

                }

            }

        }

    }

}