package com.novaember.hanzeschedule

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
import android.widget.ArrayAdapter

import kotlinx.android.synthetic.main.activity_scheduleedit.*
import kotlinx.android.synthetic.main.dialog_scheduleadd.view.*
import kotlinx.android.synthetic.main.scheduleedit_resource.view.*

class ScheduleEditActivity : AppCompatActivity() {
    lateinit var resources: MutableSet<Pair<Resource, Set<EventFilter>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduleedit)
        setSupportActionBar(toolbar)

        // Initialize Digirooster API
        val digirooster = Session.digirooster ?: Digirooster(this)
        if (Session.digirooster == null) Session.digirooster = digirooster

        val pref = getSharedPreferences("HanzeSchedule", Context.MODE_PRIVATE)
        val username = pref.getString("digirooster-username", "")
        val password = pref.getString("digirooster-password", "")

        if (username != "" && password != "") {
            // Attempt login with stored credentials
            Session.digirooster?.logIn(username, password) { success ->
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
        schedule_list.addView(view)
    }

    class ScheduleAddDialog(val scheduleEditActivity: ScheduleEditActivity) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            val view = inflater.inflate(R.layout.dialog_scheduleadd, null)

            view.staff_input.setFilters(arrayOf(InputFilter.AllCaps()))

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    Session.digirooster?.searchStaff(s.toString(), 10) { response ->
                        Handler().post {
                            val staffAdapter = ArrayAdapter<String>(activity, R.layout.dropdown_item,
                            response.keys().asSequence().map { key ->
                                response.getString(key)
                            }.toList())

                            view.staff_input.setAdapter(staffAdapter)
                            staffAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            view.staff_input.addTextChangedListener(textWatcher)

            builder.setView(view)

            builder.setPositiveButton(R.string.add) { dialog, id ->
                val staffName = view.staff_input.text.toString()
                val resource = Resource(staffName, Resource.Type.STAFF, staffName)
                scheduleEditActivity.addResourceView(resource)

                (activity as ScheduleEditActivity).resources.add(Pair(resource, setOf<EventFilter>()))
                PreferenceManager(activity).putResources((activity as ScheduleEditActivity).resources)
            }

            builder.setNegativeButton(R.string.cancel) { dialog, id -> }

            return builder.create()
        }
    }
}

