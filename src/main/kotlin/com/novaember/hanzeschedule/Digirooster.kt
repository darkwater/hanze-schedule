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
import java.nio.charset.StandardCharsets
import java.util.HashMap

import org.json.JSONObject
import org.json.JSONArray

class Digirooster(val context: Context, val baseUrl: String = "https://digirooster.hanze.nl") {
    var queue: RequestQueue
    var loggedIn: Boolean = false

    init {
        queue = Volley.newRequestQueue(context)

        CookieHandler.setDefault(CookieManager())
    }

    fun logIn(username: String, password: String, callback: (Boolean) -> Unit) {
        val onSuccess = object : Response.Listener<String> {
            override fun onResponse(response: String) {
                loggedIn = true
                callback(true)
            }
        }

        val onError = object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                Log.e("HanzeSchedule", "Something went wrong during login", error)
                // Log.e("HanzeSchedule", error.networkResponse.data.toString(StandardCharsets.UTF_8))
                callback(false)
            }
        }

        val url = baseUrl + "/CookieAuth.dll?Logon"
        val stringRequest = object : StringRequest(Request.Method.POST, url, onSuccess, onError) {
            override fun getParams(): HashMap<String, String> {
                return hashMapOf(
                        "curl" to "Z2FwebsiteZ2F",
                        "username" to username,
                        "password" to password
                )
            }
        }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

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

        val request = JsonObjectRequest(Request.Method.POST, baseUrl + "/website/AjaxService.asmx/$action",
        requestBody, successListener, errorListener)

        // Add the request to the RequestQueue.
        queue.add(request)
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
