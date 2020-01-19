package com.glados.navtag.ui.running

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.glados.navtag.R
import com.glados.navtag.core.*
import kotlinx.android.synthetic.main.item_preset.view.*


class RunningPresetView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var preset: RunningPreset

    init {
        LayoutInflater.from(context).inflate(R.layout.item_preset, this, true)
        deleteButton.setOnClickListener {
            RunningList.removeElement(preset)
        }
        uploadButton.setOnClickListener {
            NavTagList.addElement(NavTagPreset(preset.name, NavTagMode.Running, null))
        }
    }

    fun setupView(preset: RunningPreset) {
        this.preset = preset
        name.text = preset.name
    }

}