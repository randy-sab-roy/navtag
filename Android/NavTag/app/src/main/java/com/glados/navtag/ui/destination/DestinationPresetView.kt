package com.glados.navtag.ui.destination

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.glados.navtag.R
import com.glados.navtag.core.DestinationList
import com.glados.navtag.core.DestinationPreset
import com.glados.navtag.core.NavTagList
import com.glados.navtag.core.NavTagPreset
import kotlinx.android.synthetic.main.item_preset.view.*

class DestinationPresetView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var preset: DestinationPreset

    init {
        LayoutInflater.from(context).inflate(R.layout.item_preset, this, true)
        deleteButton.setOnClickListener {
            DestinationList.removeElement(preset)
        }
        uploadButton.visibility = View.GONE
    }

    fun setupView(preset: DestinationPreset) {
        this.preset = preset
        name.text = preset.name
    }

}