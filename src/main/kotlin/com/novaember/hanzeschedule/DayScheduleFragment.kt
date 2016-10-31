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

import java.util.Calendar

import kotlinx.android.synthetic.main.fragment_dayschedule.view.*
import kotlinx.android.synthetic.main.fragment_dayschedule_item.view.*

class DayScheduleFragment() : Fragment() {
    val startHour  = 8
    val endHour    = 20
    val hourHeight = 96 // in dp

    lateinit var day: Calendar
    lateinit var nextDay: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val week = Session.activeSchedule!!.getWeek(arguments.getInt("weekNumber"))
        day = week.days[arguments.getInt("day")]
        nextDay = week.days[arguments.getInt("day") + 1]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_dayschedule, container, false)
        val schedule = Session.activeSchedule

        if (schedule == null) {
            return view
        }

        fun addItem(height: Int, color: Int, text: String,
                    centeredText: Boolean = false,
                    nubSize:      Int     = 0,
                    lineWidth:    Int     = 2) {
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
        val headerText = "<font color=\"#707070\">${day.format("EEEE d MMMM yyyy")}</font>"
        addItem(64, timelineNeutralColor, headerText, true)

        // Events
        val events = schedule.events.filter { (day..nextDay).contains(it.start) }

        if (!events.isEmpty()) {
            var lastEnd = events.first().end.hourFloat()
            events.forEach { event ->
                if (event.start.hourFloat() > lastEnd) {
                    addItem(64, timelineNeutralColor, "")
                }

                val height = 172 // (event.duration * hourHeight).toInt()
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
