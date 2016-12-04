package com.novaember.hanzeschedule

import android.content.Context
import android.util.Log

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.HashMap

import org.json.JSONObject
import org.json.JSONArray

/**
 * Functions to interface with Digirooster.
 */
object Digirooster {

    val baseUrl = "https://digirooster.hanze.nl"

    /**
     * Generic Digirooster AjaxService call
     *
     * @param action      Name of action
     * @param requestBody Request data
     * @param callback    Called upon success
     */
    fun ajaxService(action: String, requestBody: JSONObject?, callback: (String) -> Unit) {
        val successListener = object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject) {
                callback(response.getString("d"))
            }
        }

        val errorListener = object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                Log.e("HanzeSchedule", "Something went wrong during $action", error)
                // Log.e("HanzeSchedule", error.networkResponse.data.toString(StandardCharsets.UTF_8))
                // callback(null)
            }
        }

        val url = baseUrl + "/website/AjaxService.asmx/$action"
        val request = JsonObjectRequest(Request.Method.POST, url, requestBody, successListener, errorListener)

        // Add the request to the RequestQueue.
        Session.requestQueue.add(request)
    }

    fun getResourceSchedule(resourceId: String, resourceType: Resource.Type, callback: (ResourceSchedule) -> Unit) {
        val requestBody = JSONObject(mapOf(
                "ResourceID" to resourceId,
                "Resource"   to  resourceType.value
        ))

        ajaxService("GetSchedule", requestBody) { response ->
            callback(ResourceSchedule(JSONObject(response)))
        }
    }

    fun searchStaff(searchString: String, count: Int, callback: (List<Resource>) -> Unit) {
        val requestBody = JSONObject(mapOf(
                "SearchString" to searchString,
                "Count"        to count.toString()
        ))

        ajaxService("SearchStaff", requestBody) { response ->
            callback(JSONObject(response).toStringStringPairList().map {
                Resource(it.first, Resource.Type.STAFF, it.second)
            })
        }
    }

    fun getSchools(callback: (List<School>) -> Unit) {
        ajaxService("GetSchools", JSONObject()) { response ->
            callback(JSONArray(response).toJSONObjectList().map { School(it) })
        }
    }

    fun getClasses(school: School, year: Int, callback: (List<Resource>) -> Unit) {
        val requestBody = JSONObject(mapOf(
                "SchoolID"  to school.id,
                "StudyYear" to year
        ))

        ajaxService("GetGroups", requestBody) { response ->
            callback(JSONObject(response).toStringStringPairList().map {
                Resource(it.first, Resource.Type.CLASS, it.second)
            })
        }
    }
}
