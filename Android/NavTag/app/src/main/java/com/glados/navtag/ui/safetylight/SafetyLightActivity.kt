package com.glados.navtag.ui.safetylight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import com.glados.navtag.R
import com.glados.navtag.core.BlinkRate
import com.glados.navtag.core.SafetyLight
import com.glados.navtag.core.SafetyLightPreset
import kotlinx.android.synthetic.main.activity_safety_light.*

class SafetyLightActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Create New Safety Light Preset"
        setContentView(R.layout.activity_safety_light)
    }

    override fun onStart() {
        super.onStart()
        saveButton.setOnClickListener {
            if (name.text.toString().isNotEmpty()) {
                SafetyLight.addElement(
                    SafetyLightPreset(
                        name.text.toString(),
                        BlinkRate.valueOf(spinner.selectedItem.toString())
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
