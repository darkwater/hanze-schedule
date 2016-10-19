package com.novaember.hanzeschedule

import android.content.Context
import android.graphics.Color
import android.util.TypedValue

import java.text.SimpleDateFormat
import java.util.Calendar

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
    calendar.timeInMillis = this + 2 * 3600 * 1000 // how to timezone
    return calendar
}

fun Calendar.hourFloat(): Float {
    return (timeInMillis.toDouble() / (3600 * 1000) % 24).toFloat()
}
