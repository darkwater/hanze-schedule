package com.novaember.hanzeschedule

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView

class SchedulePagerAdapter(fm: FragmentManager, val schedule: Schedule) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return schedule.weeks.size
    }

    override fun getItem(weekNumber: Int): Fragment {
        val fragment = ScheduleFragment(schedule)
        val bundle = Bundle()
        bundle.putInt("weekNumber", weekNumber)
        fragment.setArguments(bundle)

        return fragment
    }

    override fun getPageTitle(weekNumber: Int): String {
        return schedule.weeks[weekNumber].number.toString()
    }

    class ScheduleFragment(val schedule: Schedule) : Fragment() {
        var weekNumber: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            weekNumber = getArguments()?.getInt("weekNumber") ?: 0
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val view = inflater.inflate(R.layout.fragment_schedule, container, false)

            val textView = view.findViewById(R.id.text) as TextView
            textView.text = "Week " + schedule.weeks[weekNumber].number

            return view
        }
    }
}
