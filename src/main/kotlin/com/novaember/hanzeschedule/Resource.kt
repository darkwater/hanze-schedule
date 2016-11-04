package com.novaember.hanzeschedule

class Resource(val id: String, val type: Resource.Type, val label: String) {
    enum class Type(val value: String) {
        STAFF("1"),
        CLASS("2")
    }

    fun fetchSchedule(callback: (ResourceSchedule) -> Unit) {
        Session.digirooster!!.getResourceSchedule(id, type, callback)
    }
}
