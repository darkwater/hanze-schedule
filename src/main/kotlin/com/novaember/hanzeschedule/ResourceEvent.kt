package com.novaember.hanzeschedule

import java.util.Calendar

import org.json.JSONObject

class ResourceEvent(json: JSONObject) {
    val id          = json.getString("ID")
    val description = json.getString("Description")
    val location    = json.getString("Location")
    val staff       = json.getString("Staff")
    val student     = json.getString("Student")
    val start       = json.getLong("Start").toCalendar()
    val end         = json.getLong("End").toCalendar()
    val week        = json.getInt("Week")

    val dayOfWeek = (Calendar.MONDAY..Calendar.FRIDAY).indexOf(start.get(Calendar.DAY_OF_WEEK))
    val duration  = end.hourFloat() - start.hourFloat()

    val shortDesc = description.substringAfter('/').take(3)
    val color     = location.toColor()
}
