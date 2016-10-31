package com.novaember.hanzeschedule

import java.util.Calendar

data class Week(val number: Int, val start: Calendar, val end: Calendar) {

    // Get the dates for this week (at 0:00)
    val days: List<Calendar> by lazy {
        (0 until 7).map {
            val calendar = start.clone() as Calendar
            calendar.add(Calendar.DAY_OF_WEEK, it)
            calendar
        }
    }

    fun contains(time: Calendar): Boolean {
        return (start..end).contains(time)
    }
}
