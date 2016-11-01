package com.novaember.hanzeschedule

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView

import com.android.volley.Response

import kotlinx.android.synthetic.main.activity_weekschedule.*

class WeekScheduleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekschedule)
        setSupportActionBar(toolbar)

        if (Session.digirooster == null) {
            // Initialize Digirooster API
            Session.digirooster = Digirooster(this)

            // Check for stored credentials
            val pref = getSharedPreferences("HanzeSchedule", Context.MODE_PRIVATE)
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
            scheduleViewPager.adapter = WeekSchedulePagerAdapter(this)
        }

        scheduleViewPager.pageMargin = dpToPx(1)
        scheduleViewPager.setPageMarginDrawable(ColorDrawable(resources.getColor(R.color.weekschedule_lines, null)))
    }

    fun logIn(username: String, password: String) {
        Session.digirooster?.logIn(username, password) { response ->
            Resource("99030BAD69623C854AA2CF1AB103A3C7", Resource.Type.CLASS).getSchedule { classSchedule ->
                Resource("DIRN", Resource.Type.STAFF).getSchedule { staffSchedule ->
                    val classScheduleFiltered = FilteredScheduleSource(classSchedule, setOf(ExclusiveEventFilter()))
                    val staffScheduleFiltered = FilteredScheduleSource(staffSchedule, setOf(SelectiveEventFilter()))
                    val schedule = Schedule(setOf(classScheduleFiltered, staffScheduleFiltered))
                    Session.activeSchedule = schedule

                    val weekSchedulePagerAdapter = WeekSchedulePagerAdapter(this)
                    scheduleViewPager.adapter = weekSchedulePagerAdapter

                    // Show the week containing the next event
                    val currentTime = System.currentTimeMillis()
                    val nextEvent = schedule.events.find { it.start.timeInMillis > currentTime }!!
                    scheduleViewPager.currentItem = weekSchedulePagerAdapter.getWeekIndex(nextEvent.week)
                }
            }
        }
    }

    fun showDaySchedule(week: Week, day: Int = 1) {
        val intent = Intent(this, DayScheduleActivity::class.java)
        intent.putExtra("weekNumber", week.number)
        intent.putExtra("day", day)
        startActivity(intent)
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
