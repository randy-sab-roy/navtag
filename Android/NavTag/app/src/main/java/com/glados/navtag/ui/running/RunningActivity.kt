package com.glados.navtag.ui.running

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.glados.navtag.R
import com.glados.navtag.core.RunningList
import com.glados.navtag.core.RunningPreset
import kotlinx.android.synthetic.main.activity_running.*
import kotlinx.android.synthetic.main.activity_running.name
import kotlinx.android.synthetic.main.activity_running.saveButton

class RunningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Create New Running Preset"
        setContentView(R.layout.activity_running)
    }

    override fun onStart() {
        super.onStart()
        saveButton.setOnClickListener {
            if (name.text.toString().isEmpty()) {
                name.error = "Preset name can't be empty"
            } else if(distance.text.toString().isEmpty()){
                distance.error = "Preset distance can't be empty"
            } else if(speed.text.toString().isEmpty()){
                speed.error = "Preset speed can't be empty"
            }
            else {
                RunningList.addElement(
                    RunningPreset(
                        name.text.toString(),
                        name.text.toString()
                    )
                )
                super.onBackPressed()
            }
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.speed_choice,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            km.adapter = adapter
        }

    }
}
