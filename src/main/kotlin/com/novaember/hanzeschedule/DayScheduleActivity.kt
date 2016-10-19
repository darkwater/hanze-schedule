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

class DayScheduleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dayschedule)
        setSupportActionBar(toolbar)

        val weekIndex = intent.getIntExtra("weekIndex", 0)
        val day = intent.getIntExtra("day", 1)
        scheduleViewPager.adapter = DaySchedulePagerAdapter(Session.activeSchedule!!.weeks[weekIndex], this)
        scheduleViewPager.currentItem = day - 1
    }
}
