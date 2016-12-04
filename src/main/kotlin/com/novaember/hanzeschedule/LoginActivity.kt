package com.novaember.hanzeschedule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : Activity() {
    var loading: Boolean = false
        set(value) {
            field = value
            loading_overlay.visibility = if(value) View.VISIBLE else View.INVISIBLE
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Session.initialize(this)

        forgot_password_button.setOnClickListener { view ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://password.hanze.nl/"))
            startActivity(browserIntent)
        }

        login_button.setOnClickListener { view ->
            loading = true
            login_button.setEnabled(false)

            val username = username_input.text.toString()
            val password = password_input.text.toString()

            Hanze.logIn(username, password) { success ->
                if (success) {
                    val pref = getSharedPreferences("HanzeSchedule", Context.MODE_PRIVATE)
                    val editor = pref.edit()
                    editor.putString("digirooster-username", username)
                    editor.putString("digirooster-password", password)
                    editor.commit()

                    val intent = Intent(this, WeekScheduleActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                    startActivity(intent)
                    finish()
                } else {
                    error_message.text = getString(R.string.login_error_generic)
                    error_message.visibility = View.VISIBLE

                    loading = false
                    login_button.setEnabled(true)
                }
            }
        }
    }
}
