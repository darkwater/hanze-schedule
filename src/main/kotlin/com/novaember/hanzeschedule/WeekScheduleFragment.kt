package com.novaember.hanzeschedule

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout.LayoutParams
import android.widget.TextView

import java.util.Date

import kotlinx.android.synthetic.main.fragment_weekschedule.view.*
import kotlinx.android.synthetic.main.fragment_weekschedule_hourmarker.view.*
import kotlinx.android.synthetic.main.fragment_weekschedule_event.view.*

class WeekScheduleFragment() : Fragment() {
    lateinit var week: Week

    val startHour  = 8
    val endHour    = 20
    val hourHeight = 64 // in dp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        week = Session.activeSchedule!!.getWeek(arguments.getInt("weekNumber"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_weekschedule, container, false)
        val schedule = Session.activeSchedule

        if (schedule == null) {
            return view
        }

        // Add hour markers to the first column
        val hourmarkerHolder = view.hourmarker_holder
        (startHour until endHour).forEach {
            val hourmarker = inflater.inflate(R.layout.fragment_weekschedule_hourmarker, hourmarkerHolder, false)

            // Set position
            val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            layoutParams.topMargin = context.dpToPx((it - startHour) * hourHeight)
            hourmarker.layoutParams = layoutParams

            // Set text
            hourmarker.hourmarker_label.text = "${it}h"

            hourmarkerHolder.addView(hourmarker)
        }

        // Set column header texts
        (0 until 5).forEach {
            (view.column_headers.getChildAt(it + 1) as TextView).text = "${week.days[it].format("d MMM")}"
        }

        // Populate the overview with events
        schedule.eventsInWeek(week).forEach { event ->
            // Find the column to hold this view
            val column = view.weekschedule_daycolumns.getChildAt(event.dayOfWeek + 1) as ViewGroup
            val eventView = inflater.inflate(R.layout.fragment_weekschedule_event, column, false)

            // Set event view size and position
            val top = context.dpToPx(((event.start.hourFloat() - startHour) * hourHeight))
            val height = context.dpToPx(event.duration * hourHeight)
            val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
            layoutParams.topMargin = top
            eventView.layoutParams = layoutParams

            // Set color and text
            eventView.event_color.setBackgroundColor(event.color)
            eventView.primary_text.text   = event.shortDesc
            eventView.secondary_text.text = event.location

            // Add an onClick listener that links to the day schedule
            eventView.setOnClickListener({ view ->
                (activity as WeekScheduleActivity).showDaySchedule(week, event.dayOfWeek)
            })

            column.addView(eventView)
        }

        return view
    }
}
