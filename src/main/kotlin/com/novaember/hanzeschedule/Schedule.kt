package com.novaember.hanzeschedule

import java.util.Calendar

import kotlin.collections.Set

import org.json.JSONArray
import org.json.JSONObject

class Schedule(val schedules: Set<ScheduleSource>) {
    val events = schedules.map { it.events }.flatten().toSet().sortedBy { it.start }
    val weeks  = schedules.map { it.weeks  }.flatten().toSet().sortedBy { it.start }.filter { week ->
        events.any { event -> event.weekNumber == week.number }
    }


    fun getWeek(weekNumber: Int): Week {
        return weeks.find { it.number == weekNumber }!!
    }

    fun eventsInWeek(weekNumber: Int): Set<Event> {
        return events.filter { it.weekNumber == weekNumber }.toSet()
    }

    fun eventsInWeek(week: Week): Set<Event> {
        return eventsInWeek(week.number)
    }
}
