package com.example.testapp.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateTime(dateTimeString: String): String {
    val parsedDateTime = LocalDateTime.parse(dateTimeString) // ISO-8601 формат (с "T")
    return parsedDateTime.format(
        DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
    )
}