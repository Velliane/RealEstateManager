package com.openclassrooms.realestatemanager.utils

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

fun parseLocalDateTimeToString(date: LocalDateTime): String {
    return date.format(dateTimeFormatter)
}

fun parseStringDateToLocalDateTime(date: String): LocalDateTime {
    return LocalDateTime.parse(date, dateTimeFormatter)
}