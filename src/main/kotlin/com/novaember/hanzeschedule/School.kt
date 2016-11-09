package com.novaember.hanzeschedule

import org.json.JSONObject

data class School(val id: String, val shortName: String, val longName: String) {
    constructor(json: JSONObject) : this(
            json.getString("ID"),
            json.getString("ShortName"),
            json.getString("LongName")
    )

    override fun toString() = longName
}
