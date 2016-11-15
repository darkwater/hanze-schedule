package com.novaember.hanzeschedule

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView

import com.android.volley.Response

import kotlinx.android.synthetic.main.activity_weekschedule.*

/**
 * Displays a per-day schedule of a given week.
 *
 * Activity arguments:
 *     int weekNumber (required) Week number to show
 *     int day        (def. 0)   Jump to day index in 0..4
 *
 * @see DayScheduleViewPager
 * @see DayScheduleFragment
 */
class DayScheduleActivity : AppCompatActivity() {

    /**
     * Initializes the activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dayschedule)

        // Use the Toolbar's home button as a back button
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Add an adapter for this week
        val weekNumber = intent.getIntExtra("weekNumber", 0)
        scheduleViewPager.adapter = DaySchedulePagerAdapter(Session.activeSchedule!!.getWeek(weekNumber), this)

        // Jump to a given day
        val day = intent.getIntExtra("day", 0)
        scheduleViewPager.currentItem = day

        // Set activity title
        setTitle("Week $weekNumber")
    }

    /**
     * Closes this activity (go back) when pressing the Toolbar's home button.
     *
     * @return always true
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()

        return true
    }
}
