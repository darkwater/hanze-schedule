package com.novaember.hanzeschedule

import java.util.Calendar

import kotlin.collections.Set

import org.json.JSONArray
import org.json.JSONObject

class CombinedSchedule(val schedules: Set<Schedule>) : Schedule {
    override val events = schedules.map { it.events }.flatten().toSet().sortedBy { it.start }.toSet()
    override val weeks  = schedules.map { it.weeks  }.flatten().toSet().sortedBy { it.start }.filter { week ->
        events.any { event -> event.weekNumber == week.number }
    }.toSet()
}
