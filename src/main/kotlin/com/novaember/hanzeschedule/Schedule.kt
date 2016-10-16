package com.novaember.hanzeschedule

import java.util.Date

import org.json.JSONArray
import org.json.JSONObject

class Schedule(json: JSONObject) {
    val start = Date(json.getLong("ScheduleStart"))
    val end   = Date(json.getLong("ScheduleEnd"))
    val id    = json.getString("ScheduleId")

    val changes = json.getJSONArray("ChangeData")

    val events = (0 until json.getJSONArray("ActivityData").length())
    .map { Event(json.getJSONArray("ActivityData").getJSONObject(it)) }

    val weeks = (0 until json.getJSONArray("WeekData").length())
    .map { Week(json.getJSONArray("WeekData").getJSONObject(it)) }


    class Event(json: JSONObject) {
        val id          = json.getString("ID")
        val description = json.getString("Description")
        val location    = json.getString("Location")
        val staff       = json.getString("Staff")
        val student     = json.getString("Student")
        val start       = Date(json.getLong("Start"))
        val end         = Date(json.getLong("End"))
        val week        = json.getInt("Week")
        val width       = json.getInt("Width")
        val left        = json.getInt("Left")

        val dayOfWeek = start.day
        val duration  = (end.hours + end.minutes / 60f) - (start.hours + start.minutes / 60f)

        val shortDesc = description.substringAfter('/').take(3)
        val color     = location.toColor()
    }

    class Week(json: JSONObject) {
        val number = json.getInt("WeekNumber")
        val start  = Date(json.getLong("WeekStart"))
        val end    = Date(json.getLong("WeekEnd"))
    }
}
