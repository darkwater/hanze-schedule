package com.novaember.hanzeschedule

import java.util.Calendar

import org.json.JSONArray
import org.json.JSONObject

class Schedule(json: JSONObject) {
    val start = json.getLong("ScheduleStart").toCalendar()
    val end   = json.getLong("ScheduleEnd").toCalendar()
    val id    = json.getString("ScheduleId")

    val changes = json.getJSONArray("ChangeData")

    // Let's hope events and weeks are already properly sorted. Apparently sorting them here is a pain.

    val events = (0 until json.getJSONArray("ActivityData").length())
    .map { Event(it, json.getJSONArray("ActivityData").getJSONObject(it)) }

    val weeks = (0 until json.getJSONArray("WeekData").length())
    .map { Week(it, json.getJSONArray("WeekData").getJSONObject(it)) }


    class Event(val index: Int, json: JSONObject) {
        val id          = json.getString("ID")
        val description = json.getString("Description")
        val location    = json.getString("Location")
        val staff       = json.getString("Staff")
        val student     = json.getString("Student")
        val start       = json.getLong("Start").toCalendar()
        val end         = json.getLong("End").toCalendar()
        val week        = json.getInt("Week")
        val width       = json.getInt("Width")
        val left        = json.getInt("Left")

        val dayOfWeek = start.get(Calendar.DAY_OF_WEEK) - 1
        val duration  = end.hourFloat() - start.hourFloat()

        val shortDesc = description.substringAfter('/').take(3)
        val color     = location.toColor()
    }

    class Week(val index: Int, json: JSONObject) {
        val number = json.getInt("WeekNumber")
        val start  = json.getLong("WeekStart").toCalendar()
        val end    = json.getLong("WeekEnd").toCalendar()

        // Get the dates for this week (at 0:00)
        val days = (Calendar.MONDAY..Calendar.SATURDAY).map {
            val calendar = start.clone() as Calendar
            calendar.set(Calendar.DAY_OF_WEEK, it)
            calendar
        }
    }
}
