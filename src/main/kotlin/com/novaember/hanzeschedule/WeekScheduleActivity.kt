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
import android.widget.TextView

import com.android.volley.Response

import kotlinx.android.synthetic.main.activity_weekschedule.*

class WeekScheduleActivity : AppCompatActivity() {
    val loading: Boolean = true
    lateinit var drawerToggle: ActionBarDrawerToggle

    /**
     * Initialize the activity.
     *
     * - Log in to Digirooster when credentials are stored.
     * - Show LoginActivity if login failed.
     * - Load schedule if login succeeded.
     * - Initialize navigation drawer.
     *
     * @param savedInstanceState Android stuff
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekschedule)
        setSupportActionBar(toolbar)

        // Initialize Digirooster API
        val digirooster = Session.digirooster ?: Digirooster(this)
        if (Session.digirooster == null) Session.digirooster = digirooster

        if (digirooster.loggedIn) {
            showSchedule()
        } else {
            // Check for stored credentials
            val pref = getSharedPreferences("HanzeSchedule", Context.MODE_PRIVATE)
            val username = pref.getString("digirooster-username", "")
            val password = pref.getString("digirooster-password", "")

            if (username != "" && password != "") {
                // Attempt login with stored credentials
                Session.digirooster?.logIn(username, password) { success ->
                    if (success) {
                        showSchedule()
                    } else {
                        // TODO: Handle failed connection etc
                        showLoginActivity()
                    }
                }
            } else {
                showLoginActivity()
            }
        }

        // TODO: Put this somewhere else?
        navigation_drawer.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.edit_schedule -> {
                    val intent = Intent(this, ScheduleEditActivity::class.java)
                    startActivity(intent)

                    true
                }

                else -> false
            }
        }

        // Initialize navigation drawer
        drawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open, R.string.drawer_close)
        drawerToggle.syncState()
        drawer_layout.addDrawerListener(drawerToggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    /**
     * TODO: Improve this.
     */
    override fun onRestart() {
        super.onRestart()

        showSchedule()
    }

    /**
     * Pass configuration changes to the drawer toggle helper.
     *
     * @param newConfig New configuration
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        drawerToggle.onConfigurationChanged(newConfig)
    }

    /**
     * Pass drawer toggle press to the thing helper.
     *
     * @param item Menu item that was selected
     *
     * @return `true` if the drawer toggle was pressed, `false` otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return false
    }

    /**
     * Closes this activity (go back) when pressing the Toolbar's home button.
     *
     * @return Always `true`
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()

        return true
    }

    /**
     * Show LoginActivity and finish this one.
     */
    fun showLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        startActivity(intent)
        finish()
    }

    /**
     * Actually load the user's schedule. Show ScheduleEditActivity if nothing's configured.
     * Sets up the view pager and everything once the schedule has been fetched.
     */
    fun showSchedule() {
        if (PreferenceManager(this).getResources().size == 0) {
            val intent = Intent(this, ScheduleEditActivity::class.java)
            startActivity(intent)
        }

        PreferenceManager(this).getSchedule { schedule ->
            Session.activeSchedule = schedule

            val weekSchedulePagerAdapter = WeekSchedulePagerAdapter(this)

            scheduleViewPager.adapter = weekSchedulePagerAdapter
            scheduleViewPager.pageMargin = dpToPx(1)
            scheduleViewPager.setPageMarginDrawable(ColorDrawable(resources.getColor(R.color.weekschedule_lines, null)))

            // Show the week containing the next event
            val currentTime = System.currentTimeMillis()
            val nextEvent = schedule.events.find { it.start.timeInMillis > currentTime }!!
            scheduleViewPager.currentItem = weekSchedulePagerAdapter.getWeekIndex(nextEvent.weekNumber)

            loading_indicator.setVisibility(View.GONE)
        }
    }

    /**
     * Open DayScheduleActivity on a specific week and optionally a specific day.
     *
     * @param weekNumber Week number to show
     * @param day        Day to show in 0..4 (def. 0)
     */
    fun showDaySchedule(weekNumber: Int, day: Int = 0) {
        val intent = Intent(this, DayScheduleActivity::class.java)
        intent.putExtra("weekNumber", weekNumber)
        intent.putExtra("day", day)
        startActivity(intent)
    }
}
