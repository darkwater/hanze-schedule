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

class Digirooster(val context: Context, val baseUrl: String = "https://digirooster.hanze.nl") {

    var queue: RequestQueue

    init {
        queue = Volley.newRequestQueue(context)

        CookieHandler.setDefault(CookieManager())
    }

    enum class Resource(val value: String) {
        STAFF("1"),
        CLASS("2")
    }

    fun logIn(username: String, password: String, callback: (Boolean) -> Unit) {
        val successListener = object : Response.Listener<String> {
            override fun onResponse(response: String) {
                Log.d("HanzeSchedule", response)
                callback(true)
            }
        }

        val errorListener = object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                Log.e("HanzeSchedule", "Something went wrong during login", error)
                Log.e("HanzeSchedule", error.networkResponse.data.toString(StandardCharsets.UTF_8))
                callback(false)
            }
        }

        val stringRequest = object : StringRequest(Request.Method.POST,
        baseUrl + "/CookieAuth.dll?Logon", successListener, errorListener) {
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

    fun getSchedule(resourceId: String, resource: Resource, callback: (Schedule) -> Unit) {
        val successListener = object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject) {
                Log.d("HanzeSchedule", response.getString("d"))
                callback(Schedule(JSONObject(response.getString("d"))))
            }
        }

        val errorListener = object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                Log.e("HanzeSchedule", "Something went wrong during GetSchedule", error)
                Log.e("HanzeSchedule", error.networkResponse.data.toString(StandardCharsets.UTF_8))
                // callback(null)
            }
        }

        val requestBody = JSONObject()
        requestBody.put("ResourceID", resourceId)
        requestBody.put("Resource", resource.value)

        val stringRequest = JsonObjectRequest(Request.Method.POST,
        baseUrl + "/website/AjaxService.asmx/GetSchedule", requestBody, successListener, errorListener)

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}
