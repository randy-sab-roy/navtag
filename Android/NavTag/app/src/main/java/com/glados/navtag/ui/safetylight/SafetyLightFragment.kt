package com.glados.navtag.ui.safetylight

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.glados.navtag.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_safety_light.*

class SafetyLightFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_safety_light, container, false)
    }

    override fun onStart() {
        super.onStart()
        instructions.text = "To add a preset, press the button below"
        fab.setOnClickListener {
            startActivity(Intent(context, SafetyLightActivity::class.java))
        }
    }
}