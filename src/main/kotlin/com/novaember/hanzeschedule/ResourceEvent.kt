package com.novaember.hanzeschedule

import java.util.Calendar

import org.json.JSONObject

class ResourceEvent(json: JSONObject, color: Int) : Event {
    override val id          = json.getString("ID")
    override val description = json.getString("Description")
    override val location    = json.getString("Location")
    override val staff       = json.getString("Staff")
    override val student     = json.getString("Student")
    override val start       = json.getLong("Start").toCalendar()
    override val end         = json.getLong("End").toCalendar()
    override val weekNumber  = json.getInt("Week")

    override val dayOfWeek = (Calendar.MONDAY..Calendar.FRIDAY).indexOf(start.get(Calendar.DAY_OF_WEEK))
    override val duration  = end.hourFloat() - start.hourFloat()

    override val shortDesc = description.substringAfter('/').take(3)
    override val color     = color
}
