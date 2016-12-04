package com.novaember.hanzeschedule

import android.content.Context

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

import java.net.HttpCookie

/**
 * Singleton that stores information not specific to one activity.
 */
object Session {

    /** Volley RequestQueue used for HTTPS requests. */
    lateinit var requestQueue: RequestQueue

    /** Schedule that is currently selected by the user. */
    var activeSchedule: Schedule? = null

    /**
     * Authentication cookie used for calls to Hanze and Digirooster APIs.
     *
     * @see Hanze.logIn
     */
    var authCookie: HttpCookie? = null

    /**
     * Request digest for API requests that need it.
     *
     * @see Hanze.logIn
     */
    var requestDigest: String = ""


    /**
     * Initialize if needed.
     *
     * Should be called early in every activity's onCreate to make sure properties such as [requestQueue] are always
     * initialized.
     *
     * @param context A valid context such as an Activity.
     */
    fun initialize(context: Context) {
        requestQueue = Volley.newRequestQueue(context)
    }

    /**
     * See if we're logged in.
     *
     * This method checks if [authCookie] is not null. If it's not empty but our session has expired, this method will
     * still return true.
     *
     * @return True if [authCookie] is set.
     */
    fun isLoggedIn() = authCookie != null
}
