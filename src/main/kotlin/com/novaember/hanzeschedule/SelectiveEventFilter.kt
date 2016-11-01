package com.novaember.hanzeschedule

class SelectiveEventFilter : EventFilter {
    override fun shouldInclude(event: Event): Boolean {
        return event.description == "PWEB3/Webprogramming pr plus groep"
    }
}
