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

class DaySchedulePagerAdapter(val week: Schedule.Week, val activity: DayScheduleActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
    override fun getCount(): Int {
        return 5
    }

    override fun getItem(day: Int): Fragment {
        val fragment = DayScheduleFragment()
        val bundle = Bundle()
        bundle.putInt("weekNumber", week.number)
        bundle.putInt("day", day)
        fragment.setArguments(bundle)

        return fragment
    }

    override fun getPageTitle(day: Int): String {
        return week.days[day].format("d MMM")
    }
}
