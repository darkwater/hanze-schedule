package com.novaember.hanzeschedule

import android.content.Context
import android.graphics.Color
import android.util.TypedValue

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}

fun Context.dpToPx(dp: Int): Int {
    return dpToPx(dp.toFloat())
}

fun String.toColor(): Int {
    val hue = sumByDouble { it.toDouble() } * Math.PI * 1000 % 360
    return Color.HSVToColor(floatArrayOf(hue.toFloat(), 0.95f, 0.95f))
}

fun Calendar.format(fmt: String): String {
    val dateFormat = SimpleDateFormat(fmt)
    return dateFormat.format(time)
}

fun Long.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)
    return calendar
}

fun Calendar.hourFloat(): Float {
    return get(Calendar.HOUR_OF_DAY).toFloat() + get(Calendar.MINUTE).toFloat() / 60
    return ((timeInMillis - timeZone.rawOffset).toDouble() / (3600 * 1000) % 24).toFloat()
}

fun List<List<Week>>.mergeWeeks(): List<Week> {
    return fold(listOf<Week>()) { acc, next -> acc.mergeWith(next) }
}

fun List<Week>.mergeWith(other: List<Week>): List<Week> {
    val itA = 0
    val itB = 0

    return this
}
