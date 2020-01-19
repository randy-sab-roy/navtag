package com.glados.navtag.ui.running

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.glados.navtag.R
import com.glados.navtag.core.RunningList
import com.glados.navtag.core.RunningPreset
import kotlinx.android.synthetic.main.activity_running.*

class RunningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Create New Running Preset"
        setContentView(R.layout.activity_running)
    }

    override fun onStart() {
        super.onStart()
        saveButton.setOnClickListener {
            if (name.text.toString().isNotEmpty()) {
                RunningList.addElement(
                    RunningPreset(
                        name.text.toString(),
                        name.text.toString()
                    )
                )
                super.onBackPressed()
            } else {
                name.error = "Preset name can't be empty"
            }
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.spinnerstuff,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }
}
