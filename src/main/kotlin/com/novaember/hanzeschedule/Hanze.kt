package com.novaember.hanzeschedule

import android.content.Context
import android.util.Base64
import android.util.Log

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.VolleyError

import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore
import java.net.URI
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.HashMap

import org.json.JSONArray
import org.json.JSONObject

/**
 * Functions to interface with Hanze.
 */
object Hanze {

    val baseUrl = "https://www.hanze.nl"

    /**
     * Attempt to log in.
     *
     * Upon success, this will set [Session.authCookie] and [Session.requestDigest].
     *
     * @param callback Called when the login has succeeded or failed.
     */
    fun logIn(username: String, password: String, callback: (Boolean) -> Unit) {

        // Set a cookie manager to catch the auth cookie
        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)

        val onSuccess = object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject) {
                val cookie = cookieManager.cookieStore.cookies.find { it.name == "Hanzehogeschool" }
                val digest = response
                        .getJSONObject("d")
                        .getJSONObject("GetContextWebInformation")
                        .getString("FormDigestValue")

                Session.authCookie = cookie
                Session.requestDigest = digest

                callback(true)
            }
        }

        val onError = object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                Log.e("HanzeSchedule", "Something went wrong during login", error)
                Log.d("HanzeSchedule", error.networkResponse?.data?.toString(StandardCharsets.UTF_8) ?: "")
                callback(false)
            }
        }

        val url = "$baseUrl/_api/contextinfo"
        val request = object : JsonObjectRequest(Request.Method.POST, url, null, onSuccess, onError) {
            override fun getHeaders(): MutableMap<String, String> {
                val userpass = Base64.encodeToString("$username:$password".toByteArray(), Base64.DEFAULT or Base64.NO_WRAP)

                return mutableMapOf(
                        "Authorization" to "Basic $userpass",
                        "Content-Type" to "application/json",
                        "Accept" to "application/json;odata=verbose"
                )
            }
        }

        // Add the request to the RequestQueue.
        Session.requestQueue.add(request)
    }

    /**
     * Get a list of absent teachers.
     *
     * @param callback Called with list of absent teachers
     */
    fun absentTeachers(callback: (Set<String>) -> Unit) {
        val onSuccess = object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject) {
                val results = response
                        .getJSONObject("PrimaryQueryResult")
                        .getJSONObject("RelevantResults")
                        .getJSONObject("Table")
                        .getJSONArray("Rows").toJSONObjectList()
                        .map {
                            it.getJSONArray("Cells").toJSONObjectList()
                            .find { it.getString("Key") == "HanzePageTitleOWSTEXT" }
                            ?.getString("Value") ?: "wtf"
                        }
                        .toSet()

                callback(results)
            }
        }

        val onError = object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                Log.e("HanzeSchedule", "Something went wrong", error)
                Log.e("HanzeSchedule", error.networkResponse.data.toString(StandardCharsets.UTF_8))
                // callback(false)
            }
        }

        val requestBody = JSONObject(hashMapOf(
            "request" to hashMapOf(
                "RowLimit" to 100,
                "Querytext" to "ContentTypeID:0x010100C568DB52D9D0A14D9B2FDCC96666E9F2007948130EC3DB064584E219954237AF3900242457EFB8B24247815D688C526CD44D00096B408A099D45AFA800D0AC118BDF7C0011F208F5858741008ECDBA0CC53DB1E002*",
                "SelectProperties" to hashMapOf(
                    "results" to listOf(
                        "HanzePageTitleOWSTEXT",
                        "HanzePublishingDate"
                    )
                ),
                "SortList" to hashMapOf(
                    "results" to listOf(
                        hashMapOf(
                            "Property" to "HanzePublishingDate",
                            "Direction" to "1"
                        )
                    )
                )
            )
        ))

        val url = "$baseUrl/_api/search/postquery"
        val stringRequest = object : JsonObjectRequest(Request.Method.POST, url, requestBody, onSuccess, onError) {
            override fun getBodyContentType() = "application/json;odata=verbose"

            override fun getHeaders(): Map<String, String> {
                return mapOf(
                        "Cookie" to Session.authCookie.toString(),
                        "X-RequestDigest" to Session.requestDigest,
                        "Accept" to "application/json;odata=nometadata"
                )
            }
        }

        // Add the request to the RequestQueue.
        Session.requestQueue.add(stringRequest)
    }
}
