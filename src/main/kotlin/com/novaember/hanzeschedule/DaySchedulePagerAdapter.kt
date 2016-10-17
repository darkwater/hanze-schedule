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

import kotlinx.android.synthetic.main.fragment_dayschedule.view.*
import kotlinx.android.synthetic.main.fragment_dayschedule_item.view.*

class DaySchedulePagerAdapter(val week: Schedule.Week, val activity: HanzeSchedule) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
    val schedule: Schedule?
        get() = activity.activeSchedule

    override fun getCount(): Int {
        return 5
    }

    override fun getItem(day: Int): Fragment {
        val fragment = ScheduleFragment()
        val bundle = Bundle()
        bundle.putInt("weekNumber", week.number)
        bundle.putInt("day", day)
        fragment.setArguments(bundle)

        return fragment
    }

    override fun getPageTitle(day: Int): String {
        return week.days[day].format("MMM d")
    }

    class ScheduleFragment() : Fragment() {
        val schedule: Schedule?
            get() = (activity as HanzeSchedule).activeSchedule

        val startHour  = 8
        val endHour    = 20
        val hourHeight = 96 // in dp

        lateinit var day: Date
        lateinit var nextDay: Date

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val week = schedule!!.weeks.find { it.number == arguments.getInt("weekNumber") }!!
            day = week.days[arguments.getInt("day")]
            nextDay = week.days[arguments.getInt("day") + 1]
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val view = inflater.inflate(R.layout.fragment_dayschedule, container, false)
            val schedule = schedule

            if (schedule == null) {
                return view
            }

            // Events
            val events = schedule.events.filter { (day..nextDay).contains(it.start) }
            events.forEach { event ->
                val itemView = inflater.inflate(R.layout.fragment_dayschedule_item, view.container, false)

                // Size event view
                val height = context.dpToPx((event.duration * hourHeight))
                val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
                itemView.layoutParams = layoutParams

                // Set color and text
                itemView.color.setBackgroundColor(event.color)
                itemView.textview.text = event.description

                view.container.addView(itemView)
            }

            return view
        }
    }
}
