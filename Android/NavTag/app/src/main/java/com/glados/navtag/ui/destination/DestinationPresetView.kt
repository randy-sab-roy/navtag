package com.glados.navtag.ui.destination

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.glados.navtag.R
import com.glados.navtag.core.*
import kotlinx.android.synthetic.main.item_preset.view.*

class DestinationPresetView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var preset: DestinationPreset

    init {
        LayoutInflater.from(context).inflate(R.layout.item_preset, this, true)
        deleteButton.setOnClickListener {
            DestinationList.removeElement(preset)
        }
        uploadButton.setOnClickListener {
            NavTagList.addElement(NavTagPreset(preset.name, NavTagMode.Destination))
        }
    }

    fun setupView(preset: DestinationPreset) {
        this.preset = preset
        name.text = preset.name
    }

}