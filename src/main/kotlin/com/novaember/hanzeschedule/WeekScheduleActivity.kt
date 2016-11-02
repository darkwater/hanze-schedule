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
    val loading: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekschedule)
        setSupportActionBar(toolbar)

        // Initialize Digirooster API
        val digirooster = Session.digirooster ?: Digirooster(this)
        if (Session.digirooster == null) Session.digirooster = digirooster

        if (digirooster.loggedIn) {
            showSchedule()
        } else {
            // Check for stored credentials
            val pref = getSharedPreferences("HanzeSchedule", Context.MODE_PRIVATE)
            val username = pref.getString("digirooster-username", "")
            val password = pref.getString("digirooster-password", "")

            if (username != "" && password != "") {
                // Attempt login with stored credentials
                Session.digirooster?.logIn(username, password) { success ->
                    if (success) {
                        showSchedule()
                    } else {
                        showLoginActivity()
                    }
                }
            } else {
                showLoginActivity()
            }
        }
    }

    fun showLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        startActivity(intent)
        finish()
    }

    fun showSchedule() {
        Resource("99030BAD69623C854AA2CF1AB103A3C7", Resource.Type.CLASS).getSchedule { classSchedule ->
            Resource("DIRN", Resource.Type.STAFF).getSchedule { staffSchedule ->
                val classScheduleFiltered = FilteredScheduleSource(classSchedule, setOf(ExclusiveEventFilter()))
                val staffScheduleFiltered = FilteredScheduleSource(staffSchedule, setOf(SelectiveEventFilter()))
                val schedule = Schedule(setOf(classScheduleFiltered, staffScheduleFiltered))
                Session.activeSchedule = schedule

                val weekSchedulePagerAdapter = WeekSchedulePagerAdapter(this)

                scheduleViewPager.adapter = weekSchedulePagerAdapter
                scheduleViewPager.pageMargin = dpToPx(1)
                scheduleViewPager.setPageMarginDrawable(ColorDrawable(resources.getColor(R.color.weekschedule_lines, null)))

                // Show the week containing the next event
                val currentTime = System.currentTimeMillis()
                val nextEvent = schedule.events.find { it.start.timeInMillis > currentTime }!!
                scheduleViewPager.currentItem = weekSchedulePagerAdapter.getWeekIndex(nextEvent.week)
            }
        }
    }

    fun showDaySchedule(week: Week, day: Int = 1) {
        val intent = Intent(this, DayScheduleActivity::class.java)
        intent.putExtra("weekNumber", week.number)
        intent.putExtra("day", day)
        startActivity(intent)
    }
}
