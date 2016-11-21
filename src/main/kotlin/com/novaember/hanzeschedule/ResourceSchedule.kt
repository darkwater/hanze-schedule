package com.novaember.hanzeschedule

import android.util.Log

import java.util.Calendar

import org.json.JSONArray
import org.json.JSONObject

class ResourceSchedule(json: JSONObject) : ScheduleSource {
    val start = json.getLong("ScheduleStart").toCalendar()
    val end   = json.getLong("ScheduleEnd").toCalendar()
    val id    = json.getString("ScheduleId")

    val changes = json.getJSONArray("ChangeData")

    // Let's hope events and weeks are already properly sorted. Apparently sorting them here is a pain.

    override val events = (0 until json.getJSONArray("ActivityData").length()).map {
        ResourceEvent(json.getJSONArray("ActivityData").getJSONObject(it), id.toColor())
    }.toSet()

    override val weeks = (0 until json.getJSONArray("WeekData").length()).map {
        val weekJson = json.getJSONArray("WeekData").getJSONObject(it)
        val number   = weekJson.getInt("WeekNumber")
        val start    = weekJson.getLong("WeekStart").toCalendar()
        val end      = weekJson.getLong("WeekEnd").toCalendar()

        Week(number, start, end)
    }.toSet()
}
