package com.novaember.hanzeschedule

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView

import com.android.volley.Response

import kotlinx.android.synthetic.main.activity_absentteachers.*

class AbsentTeachersActivity : AppCompatActivity() {

    /**
     * Initialize the activity.
     *
     * @param savedInstanceState Android stuff
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absentteachers)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        Session.initialize(this)

        Hanze.absentTeachers { teachers ->
            val adapter = ArrayAdapter<String>(this, R.layout.absentteacher_item, teachers.toList())
            teacher_list.setAdapter(adapter)
        }
    }

    /**
     * Closes this activity (go back) when pressing the Toolbar's home button.
     *
     * @return Always true
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()

        return true
    }
}
