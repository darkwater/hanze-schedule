package com.novaember.hanzeschedule

import android.content.Context
import android.graphics.Color
import android.util.TypedValue

import java.text.SimpleDateFormat
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

fun Date.format(fmt: String): String {
    val dateFormat = SimpleDateFormat(fmt)
    return dateFormat.format(this)
}
