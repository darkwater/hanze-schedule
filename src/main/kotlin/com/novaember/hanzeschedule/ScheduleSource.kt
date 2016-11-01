package com.novaember.hanzeschedule

interface ScheduleSource {
    val events: Set<Event>
    val weeks: Set<Week>
}
