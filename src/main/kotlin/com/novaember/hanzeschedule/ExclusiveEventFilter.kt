package com.novaember.hanzeschedule

class ExclusiveEventFilter : EventFilter {
    override fun shouldInclude(event: Event): Boolean {
        return event.description != "PWEB1/Project"
    }
}
