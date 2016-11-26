package com.novaember.hanzeschedule

import android.content.Context
import android.util.Log

import org.json.JSONArray
import org.json.JSONObject

class PreferenceManager(val context: Context) {
    fun getString(key: String, default: String = ""): String {
        val pref = context.getSharedPreferences("HanzeSchedule", Context.MODE_PRIVATE)
        return pref.getString(key, default)
    }

    fun put(key: String, value: String) {
        val pref = context.getSharedPreferences("HanzeSchedule", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getResources(): Set<Pair<Resource, Set<EventFilter>>> {
        val resourcesJson = JSONArray(getString("resources", "[]"))

        return (0 until resourcesJson.length()).map {
            val resourceJson = resourcesJson.getJSONObject(it)
            Pair(
                Resource(
                    resourceJson.getString("id"),
                    Resource.Type.valueOf(resourceJson.getString("type")),
                    resourceJson.getString("label")
                ),
                (0 until resourceJson.getJSONArray("filters").length()).map {
                    val filterJson = resourceJson.getJSONArray("filters").getJSONObject(it)
                    EventFilter(
                        filterJson.getBoolean("selective"),
                        EventFilter.Matcher.valueOf(filterJson.getString("matcher")),
                        filterJson.getString("specifier")
                    )
                }.toSet()
            )
        }.toSet()
    }

    fun putResources(resources: Set<Pair<Resource, Set<EventFilter>>>) {
        val resourcesJson = resources.map { pair ->
            val (resource, filters) = pair

            val obj = JSONObject()
            val resourceObj = JSONObject()
            resourceObj.put("id", resource.id)
            resourceObj.put("type", resource.type)
            resourceObj.put("label", resource.label)
            obj.put("resource", resourceObj)

            resourceObj.put("filters", filters.map { filter ->
                val filterObj = JSONObject()
                filterObj.put("selective", filter.selective)
                filterObj.put("matcher", filter.matcher)
                filterObj.put("specifier", filter.specifier)
                filterObj
            }.toJSONArray())
            resourceObj
        }.toJSONArray()

        put("resources", resourcesJson.toString())
    }

    fun getSchedule(callback: (Schedule) -> Unit) {
        val schedules = mutableSetOf<FilteredSchedule>()
        val resources = getResources()

        resources.forEach { pair ->
            val (resource, filters) = pair

            resource.fetchSchedule { schedule ->
                val filteredSchedule = FilteredSchedule(schedule, filters)
                schedules.add(filteredSchedule)

                if (schedules.size == resources.size) {
                    callback(CombinedSchedule(schedules))
                }
            }
        }
    }
}
