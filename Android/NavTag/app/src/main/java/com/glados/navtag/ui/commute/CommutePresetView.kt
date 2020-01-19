package com.glados.navtag.ui.commute

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.glados.navtag.R
import com.glados.navtag.core.*
import kotlinx.android.synthetic.main.item_preset.view.*


class CommutePresetView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var preset: CommutePreset

    init {
        LayoutInflater.from(context).inflate(R.layout.item_preset, this, true)
        deleteButton.setOnClickListener {
            CommuteList.removeElement(preset)
        }
        uploadButton.setOnClickListener {
            NavTagList.addElement(NavTagPreset(preset.name, NavTagMode.Commute, null))
        }
    }

    fun setupView(preset: CommutePreset) {
        this.preset = preset
        name.text = preset.name
    }

}