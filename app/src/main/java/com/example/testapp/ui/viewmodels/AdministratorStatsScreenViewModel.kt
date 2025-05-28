package com.example.testapp.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.domain.models.ChartData
import com.example.testapp.domain.models.HourlyReportData
import com.example.testapp.domain.models.ReportsStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AdministratorStatsScreenViewModel @Inject constructor() : ViewModel() {

    // Текущая дата
    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> @RequiresApi(Build.VERSION_CODES.O) get() = _currentDate

    // Статистика отчетов
    private val _reportsStatistics = MutableStateFlow(ReportsStatistics(0, 0, 0, 0))
    val reportsStatistics: StateFlow<ReportsStatistics> get() = _reportsStatistics

    // Данные для графика по часам
    private val _hourlyData = MutableStateFlow<List<HourlyReportData>>(emptyList())
    val hourlyData: StateFlow<List<HourlyReportData>> get() = _hourlyData

    // Данные для графика (вычисляемое свойство)
    val chartData: StateFlow<ChartData> get() = _hourlyData.map { hourlyReports ->
        getChartData(hourlyReports) // Теперь принимает данные явно
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = getChartData(emptyList())
    )

    init {
        loadRandomStatistics(LocalDate.now())
    }

    private fun loadRandomStatistics(date: LocalDate) {
        viewModelScope.launch {
            // Генерация случайных количеств отчетов
            val random = Random(date.toEpochDay())

            val blenderCount = random.nextInt(20)
            val acidCount = random.nextInt(15)
            val gelCount = random.nextInt(25)
            val totalCount = blenderCount + acidCount + gelCount

            _reportsStatistics.value = ReportsStatistics(
                blenderCount = blenderCount,
                acidCount = acidCount,
                gelCount = gelCount,
                total = totalCount
            )

            // Генерация случайных данных по часам
            val hourlyReports = (0..23).map { hour ->
                HourlyReportData(
                    hour = hour,
                    count = random.nextInt(5)
                )
            }
            _hourlyData.value = hourlyReports
            // _chartData обновляется автоматически через преобразование
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousDate() {
        _currentDate.value = _currentDate.value.minusDays(1)
        loadRandomStatistics(_currentDate.value)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextDate() {
        _currentDate.value = _currentDate.value.plusDays(1)
        loadRandomStatistics(_currentDate.value)
    }

    // Теперь принимает данные явно
    private fun getChartData(hourlyData: List<HourlyReportData>): ChartData {
        val sessionsByHour = hourlyData.associate { it.hour to it.count }
        val maxCount = hourlyData.maxOfOrNull { it.count } ?: 0

        val yAxisLabels = when {
            maxCount <= 2 -> listOf(0, 1, 2)
            maxCount <= 5 -> listOf(0, 2, 4)
            else -> (0..maxCount step (maxCount / 3 + 1)).toList()
        }

        val xAxisLabels = (0..23).toList()

        return ChartData(
            sessionsByHour = sessionsByHour,
            yAxisLabels = yAxisLabels,
            xAxisLabels = xAxisLabels
        )
    }
}