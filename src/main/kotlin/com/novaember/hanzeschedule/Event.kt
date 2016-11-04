package com.novaember.hanzeschedule

import java.util.Calendar

interface Event {
    val id: String
    val description: String
    val location: String
    val staff: String
    val student: String
    val start: Calendar
    val end: Calendar
    val weekNumber: Int

    val dayOfWeek: Int
    val duration: Float

    val shortDesc: String
    val color: Int
}
