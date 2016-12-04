package com.novaember.hanzeschedule

data class Resource(val id: String, val type: Resource.Type, val label: String) {
    enum class Type(val value: String) {
        STAFF("1"),
        CLASS("2")
    }

    fun fetchSchedule(callback: (ResourceSchedule) -> Unit) {
        Digirooster.getResourceSchedule(id, type, callback)
    }

    override fun toString() = label
}
