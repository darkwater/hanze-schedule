package com.novaember.hanzeschedule

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
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

            fun addItem(height: Int, color: Int, text: String,
                        centeredText:  Boolean = false,
                        nubSize:       Int     = 0,
                        lineWidth:     Int     = 2) {
                val itemView = inflater.inflate(R.layout.fragment_dayschedule_item, view.container, false)

                // Size views
                itemView.layoutParams.height     = context.dpToPx(height)
                itemView.nub.layoutParams.width  = context.dpToPx(nubSize)
                itemView.nub.layoutParams.height = context.dpToPx(nubSize)
                itemView.line.layoutParams.width = context.dpToPx(lineWidth)

                // Set colors and text
                itemView.nub.setBackgroundColor(color)
                itemView.line.setBackgroundColor(color)
                itemView.textview.text = Html.fromHtml(text)

                if (centeredText) itemView.textview.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL

                view.container.addView(itemView)
            }

            val timelineNeutralColor = 0xffcdcdcd.toInt()

            // Add first item that shows the date
            val headerText = "<font color=\"#707070\">${day.format("EEEE MMMM d yyyy")}</font>"
            addItem(64, timelineNeutralColor, headerText, true)

            // Events
            val events = schedule.events.filter { (day..nextDay).contains(it.start) }

            if (!events.isEmpty()) {
                var lastEnd = events.first().end.hourFloat()
                events.forEach { event ->
                    if (event.start.hourFloat() > lastEnd) {
                        addItem(64, timelineNeutralColor, "")
                    }

                    val height = (event.duration * hourHeight).toInt()
                    val color = event.color
                    val text = """
                        |<b>${event.start.format("HH:mm")}</b> - ${event.end.format("HH:mm")}<br />
                        |<b>${event.description}</b><br />
                        |<b>${event.location}</b> &middot; ${event.staff}<br />
                        |${event.student}
                    """.trimMargin()
                    addItem(height, color, text, nubSize = 24, lineWidth = 6)
                    lastEnd = event.end.hourFloat()
                }
            }

            val itemView = inflater.inflate(R.layout.fragment_dayschedule_item, view.container, false)

            // Size views
            itemView.layoutParams.height     = 0
            (itemView.layoutParams as LinearLayout.LayoutParams).weight = 1f
            itemView.nub.layoutParams.width  = 0
            itemView.nub.layoutParams.height = 0
            itemView.line.layoutParams.width = context.dpToPx(2)

            // Set colors and text
            itemView.line.setBackgroundColor(timelineNeutralColor)

            if (events.isEmpty()) {
                itemView.textview.text = "No events for this day!"
            }

            view.container.addView(itemView)

            return view
        }
    }
}
