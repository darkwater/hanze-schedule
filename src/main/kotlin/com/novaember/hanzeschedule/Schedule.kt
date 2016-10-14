package com.novaember.hanzeschedule

import java.util.Date

import org.json.JSONArray
import org.json.JSONObject

class Schedule(json: JSONObject) {
    val start = Date(json.getLong("ScheduleStart"))
    val end   = Date(json.getLong("ScheduleEnd"))
    val id    = json.getString("ScheduleId")

    val changes = json.getJSONArray("ChangeData")

    val activities = (0 until json.getJSONArray("ActivityData").length()).asSequence()
    .map { Activity(json.getJSONArray("ActivityData").getJSONObject(it)) }.toList()

    val weeks = (0 until json.getJSONArray("WeekData").length()).asSequence()
    .map { Week(json.getJSONArray("WeekData").getJSONObject(it)) }.toList()


    class Activity(json: JSONObject) {
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
    }

    class Week(json: JSONObject) {
        val number = json.getInt("WeekNumber")
        val start  = Date(json.getLong("WeekStart"))
        val end    = Date(json.getLong("WeekEnd"))
    }
}
