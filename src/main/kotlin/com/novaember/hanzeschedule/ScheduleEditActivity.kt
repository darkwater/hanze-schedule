package com.novaember.hanzeschedule

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_scheduleedit.*
import kotlinx.android.synthetic.main.dialog_scheduleadd.view.*
import kotlinx.android.synthetic.main.scheduleedit_resource.view.*

class ScheduleEditActivity : AppCompatActivity() {
    lateinit var resources: MutableSet<Pair<Resource, Set<EventFilter>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduleedit)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        Session.initialize(this)

        val pref = getSharedPreferences("HanzeSchedule", Context.MODE_PRIVATE)
        val username = pref.getString("digirooster-username", "")
        val password = pref.getString("digirooster-password", "")

        if (username != "" && password != "") {
            // Attempt login with stored credentials
            Hanze.logIn(username, password) { success ->
            }
        }

        resources = PreferenceManager(this).getResources().toMutableSet()
        resources.forEach { pair ->
            val (resource, filters) = pair
            addResourceView(resource, filters)
        }

        add_new.setOnClickListener {
            val dialog = ScheduleAddDialog(this)
            dialog.show(supportFragmentManager, "schedule-add")
        }
    }

    fun addResourceView(resource: Resource, filters: Set<EventFilter> = setOf<EventFilter>()) {
        val view = layoutInflater.inflate(R.layout.scheduleedit_resource, null)
        view.label.text = resource.label
        view.schedule_color.setBackgroundColor(resource.label.toColor())

        view.setOnClickListener { view ->
            schedule_list.removeView(view)

            resources.remove(Pair(resource, filters))
            PreferenceManager(this).putResources(resources)
        }

        schedule_list.addView(view)
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

    class ScheduleAddDialog(val scheduleEditActivity: ScheduleEditActivity) : DialogFragment() {
        var selectedYear = 0

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            val view = inflater.inflate(R.layout.dialog_scheduleadd, null)

            // Class input
            Digirooster.getSchools { schools ->
                view.school_spinner.adapter = ArrayAdapter(scheduleEditActivity, R.layout.scheduleedit_school, schools)
            }

            fun updateClassSpinner() {
                val school = view.school_spinner.selectedItem as School

                Digirooster.getClasses(school, selectedYear) { classes ->
                    view.class_spinner.adapter = ArrayAdapter(scheduleEditActivity,
                    R.layout.scheduleedit_school, classes)
                }
            }

            view.school_spinner.setOnItemSelectedListener( object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(spinner: AdapterView<out Adapter>, item: View, position: Int, id: Long) {
                    if (selectedYear != 0) updateClassSpinner()
                }

                override fun onNothingSelected(spinner: AdapterView<out Adapter>) {}
            })


            setOf(view.school_year1, view.school_year2, view.school_year3, view.school_year4).forEachIndexed { index, button ->
                button.setOnClickListener {
                    val year = index + 1
                    if (selectedYear != year) {
                        selectedYear = year

                        updateClassSpinner()
                    }
                }
            }

            view.class_add.setOnClickListener {
                val resource = view.class_spinner.selectedItem as Resource
                scheduleEditActivity.addResourceView(resource)

                scheduleEditActivity.resources.add(Pair(resource, setOf<EventFilter>()))
                PreferenceManager(scheduleEditActivity).putResources(scheduleEditActivity.resources)

                dismiss()
            }

            // Teacher input
            view.staff_input.setFilters(arrayOf(InputFilter.AllCaps()))
            view.staff_input.addTextChangedListener(StaffInputTextWatcher(activity, view.staff_input))

            builder.setView(view)

            builder.setPositiveButton(R.string.add) { dialog, id ->
                val staffName = view.staff_input.text.toString()
                val resource = Resource(staffName, Resource.Type.STAFF, staffName)
                scheduleEditActivity.addResourceView(resource)

                (activity as ScheduleEditActivity).resources.add(Pair(resource, setOf<EventFilter>()))
                PreferenceManager(activity).putResources((activity as ScheduleEditActivity).resources)
            }

            return builder.create()
        }
    }

    class StaffInputTextWatcher(val context: Context, val staff_input: AutoCompleteTextView) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            Digirooster.searchStaff(s.toString(), 10) { response ->
                Handler().post {
                    val staffAdapter = ArrayAdapter(context, R.layout.dropdown_item, response)
                    staff_input.setAdapter(staffAdapter)
                    staffAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
