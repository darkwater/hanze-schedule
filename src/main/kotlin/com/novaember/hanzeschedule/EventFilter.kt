package com.novaember.hanzeschedule

interface EventFilter {
    fun shouldInclude(event: Event): Boolean
}
