package com.novaember.hanzeschedule

import java.util.Calendar

import kotlin.collections.Set

import org.json.JSONArray
import org.json.JSONObject

class Schedule(schedules: Set<ResourceSchedule>) {
    val events = schedules.map { it.events }.flatten().toSet().sortedBy { it.start }
    val weeks  = schedules.map { it.weeks  }.flatten().toSet().sortedBy { it.start }


    fun getWeek(weekNumber: Int): Week {
        return weeks.find { it.number == weekNumber }!!
    }

    fun eventsInWeek(weekNumber: Int): List<ResourceEvent> {
        return events.filter { it.week == weekNumber }
    }

    fun eventsInWeek(week: Week): List<ResourceEvent> {
        return eventsInWeek(week.number)
    }
}
