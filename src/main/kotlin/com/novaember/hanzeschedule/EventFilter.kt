package com.novaember.hanzeschedule

class EventFilter(val selective: Boolean, val matcher: EventFilter.Matcher, val specifier: String) {
    enum class Matcher() {
        DESCRIPTION,
        ID
    }

    fun shouldInclude(event: Event): Boolean {
        return when (matcher) {
            Matcher.DESCRIPTION -> event.description.matches(Regex(specifier))
            Matcher.ID          -> event.id == specifier
        } == selective
    }
}
