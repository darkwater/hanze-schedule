package com.novaember.hanzeschedule

import kotlin.collections.Set

class FilteredSchedule(val schedule: Schedule, val filters: Set<EventFilter>) : Schedule {

    override val events: Set<Event>
        get() {
            return schedule.events.filter { event ->
                filters.all { filter ->
                    filter.shouldInclude(event)
                }
            }.toSet()
        }

    override val weeks: Set<Week>
        get() = schedule.weeks
}
