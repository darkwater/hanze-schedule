package com.novaember.hanzeschedule

import kotlin.collections.Set

class FilteredScheduleSource(val scheduleSource: ScheduleSource, val filters: Set<EventFilter>) : ScheduleSource {

    override val events: Set<Event>
        get() {
            return scheduleSource.events.filter { event ->
                filters.all { filter ->
                    filter.shouldInclude(event)
                }
            }.toSet()
        }

    override val weeks: Set<Week>
        get() = scheduleSource.weeks
}
