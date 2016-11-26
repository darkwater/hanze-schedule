package com.novaember.hanzeschedule

interface Schedule {
    val events: Set<Event>
    val weeks: Set<Week>

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
