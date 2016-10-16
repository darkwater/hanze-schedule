package com.novaember.hanzeschedule

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.TextView

import com.android.volley.Response

import kotlinx.android.synthetic.main.activity_main.scheduleViewPager

class HanzeSchedule : FragmentActivity() {
    var dataFragment: DataFragment? = null

    var digirooster: Digirooster
        get() = dataFragment!!.digirooster
        set(value) { dataFragment!!.digirooster = value }

    var activeSchedule: Schedule?
        get() = dataFragment?.activeSchedule
        set(value) { dataFragment?.activeSchedule = value }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataFragment = supportFragmentManager.findFragmentByTag("data") as DataFragment?

        if (dataFragment == null) {
            dataFragment = DataFragment()
            supportFragmentManager.beginTransaction().add(dataFragment, "data").commit()

            // Initialize Digirooster API
            digirooster = Digirooster(this)

            // Check for stored credentials
            val pref = getPreferences(Context.MODE_PRIVATE)
            val username = pref.getString("digirooster-username", "")
            val password = pref.getString("digirooster-password", "")

            if (username != "" && password != "") {
                // Log in with stored credentials
                logIn(username, password)
            } else {
                // Ask for credentials
                val loginDialog = LoginCredentialsDialog()

                loginDialog.onLogin { username, password ->
                    logIn(username, password)

                    // Store credentials
                    val editor = pref.edit()
                    editor.putString("digirooster-username", username)
                    editor.putString("digirooster-password", password)
                    editor.commit()
                }

                loginDialog.onQuit {
                    finish()
                }

                loginDialog.show(fragmentManager, "login")
            }
        } else {
            scheduleViewPager.adapter = SchedulePagerAdapter(this)
        }

        scheduleViewPager.pageMargin = dpToPx(1)
        scheduleViewPager.setPageMarginDrawable(ColorDrawable(resources.getColor(R.color.weekschedule_lines, null)))
    }

    private fun logIn(username: String, password: String) {
        digirooster.logIn(username, password) { response ->
            digirooster.getSchedule("DIRN", Digirooster.Resource.STAFF) { schedule ->
                activeSchedule = schedule
                scheduleViewPager.adapter = SchedulePagerAdapter(this)
            }
        }
    }

    class DataFragment : Fragment() {
        lateinit var digirooster: Digirooster
        var activeSchedule: Schedule? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }
    }

    class LoginCredentialsDialog : DialogFragment() {
        var onLoginCallback: (String, String) -> Unit = { username, password -> }
        var onQuitCallback: () -> Unit = {}

        fun onLogin(callback: (String, String) -> Unit) {
            onLoginCallback = callback
        }

        fun onQuit(callback: () -> Unit) {
            onQuitCallback = callback
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(getActivity())
            val inflater = getActivity().getLayoutInflater()

            builder.setTitle("Log in")
            builder.setView(inflater.inflate(R.layout.dialog_login, null))

            builder.setPositiveButton(R.string.log_in) { dialog, id ->
                val username = getDialog().findViewById(R.id.username) as TextView
                val password = getDialog().findViewById(R.id.password) as TextView
                onLoginCallback(username.text.toString(), password.text.toString())
            }

            builder.setNegativeButton(R.string.quit) { dialog, id ->
                onQuitCallback()
            }

            return builder.create()
        }
    }
}
