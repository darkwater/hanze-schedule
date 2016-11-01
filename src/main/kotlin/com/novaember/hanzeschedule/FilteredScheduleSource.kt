package com.novaember.hanzeschedule

import kotlin.collections.Set

class FilteredScheduleSource(val scheduleSource: ScheduleSource, val filters: Set<EventFilter>) {

    val events: Set<Event>
        get() {
            return scheduleSource.events.filter { event ->
                filters.any { filter ->
                    filter.shouldInclude(event)
                }
            }.toSet()
        }

    val weeks: Set<Week>
        get() = scheduleSource.weeks
}
