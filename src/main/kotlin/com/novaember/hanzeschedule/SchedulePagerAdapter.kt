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

class SchedulePagerAdapter(val activity: HanzeSchedule) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
    val schedule: Schedule?
        get() = activity.activeSchedule

    override fun getCount(): Int {
        return schedule?.weeks?.size ?: 0
    }

    override fun getItem(weekIndex: Int): Fragment {
        val fragment = ScheduleFragment()
        val bundle = Bundle()
        bundle.putInt("weekIndex", weekIndex)
        fragment.setArguments(bundle)

        return fragment
    }

    override fun getPageTitle(weekIndex: Int): String {
        return schedule?.weeks?.get(weekIndex)?.number.toString()
    }

    class ScheduleFragment() : Fragment() {
        val schedule: Schedule?
            get() = (activity as HanzeSchedule).activeSchedule

        lateinit var week: Schedule.Week

        val startHour  = 8
        val endHour    = 20
        val hourHeight = 64 // in dp

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            week = schedule!!.weeks[arguments.getInt("weekIndex")]
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val view = inflater.inflate(R.layout.fragment_weekschedule, container, false)
            val schedule = schedule

            if (schedule == null) {
                return view
            }

            // Hour markers
            val hourmarkerHolder = view.hourmarker_holder
            (startHour until endHour).forEach {
                val hourmarker = inflater.inflate(R.layout.fragment_weekschedule_hourmarker, hourmarkerHolder, false)

                // Position marker
                val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                layoutParams.topMargin = context.dpToPx((it - startHour) * hourHeight)
                hourmarker.layoutParams = layoutParams

                hourmarker.hourmarker_label.text = "${it}h"

                hourmarkerHolder.addView(hourmarker)
            }

            // Set column header texts
            (1..5).forEach {
                (view.column_headers.getChildAt(it) as TextView).text = "${week.days[it - 1].format("MMM d")}"
            }

            // Events
            val events = schedule.events.filter { (week.start..week.end).contains(it.start) }
            events.forEach { event ->
                // Column to hold this view
                val column = view.weekschedule_daycolumns.getChildAt(event.dayOfWeek) as ViewGroup
                val eventView = inflater.inflate(R.layout.fragment_weekschedule_event, column, false)

                // Position and size event view
                val top = context.dpToPx((((event.start.hours + event.start.minutes / 60f) - startHour) * hourHeight))
                val height = context.dpToPx((event.duration * hourHeight + 1))
                val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
                layoutParams.topMargin = top
                eventView.layoutParams = layoutParams

                // Set color and text
                eventView.event_color.setBackgroundColor(event.color)
                eventView.primary_text.text   = event.shortDesc
                eventView.secondary_text.text = event.location

                // Go to this day on click
                eventView.setOnClickListener({ view ->
                    (activity as HanzeSchedule).showDaySchedule(week)
                })

                column.addView(eventView)
            }

            return view
        }
    }
}
