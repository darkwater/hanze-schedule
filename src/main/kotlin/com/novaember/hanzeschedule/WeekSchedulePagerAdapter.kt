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
    override fun getCount(): Int {
        return Session.activeSchedule?.weeks?.size ?: 0
    }

    override fun getItem(weekIndex: Int): Fragment {
        val fragment = WeekScheduleFragment()
        val bundle = Bundle()
        bundle.putInt("weekIndex", weekIndex)
        fragment.setArguments(bundle)

        return fragment
    }

    override fun getPageTitle(weekIndex: Int): String {
        return Session.activeSchedule!!.weeks[weekIndex].number.toString()
    }
}
