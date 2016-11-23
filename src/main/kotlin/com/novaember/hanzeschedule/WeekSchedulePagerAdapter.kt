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

class WeekSchedulePagerAdapter(val activity: WeekScheduleActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
    val weeks = Session.activeSchedule!!.weeks.toList()
    val currentWeek = weeks.find { it.contains(System.currentTimeMillis().toCalendar()) }

    override fun getCount(): Int {
        return weeks.size
    }

    override fun getItem(weekIndex: Int): Fragment {
        val fragment = WeekScheduleFragment()
        val bundle = Bundle()
        bundle.putInt("weekNumber", weeks[weekIndex].number)
        fragment.setArguments(bundle)

        return fragment
    }

    override fun getPageTitle(weekIndex: Int): String {
        val week = weeks[weekIndex]
        val weekNumber = week.number

        if (week == currentWeek) {
            return "($weekNumber)"
        } else {
            return "$weekNumber"
        }
    }

    fun getWeekIndex(weekNumber: Int): Int {
        return weeks.indexOfFirst { it.number == weekNumber }
    }
}
