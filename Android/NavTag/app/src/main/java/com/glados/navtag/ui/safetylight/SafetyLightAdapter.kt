package com.glados.navtag.ui.safetylight

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glados.navtag.core.SafetyLight
import com.glados.navtag.core.SafetyLightPreset

class SafetyLightAdapter(private val songs: ArrayList<SafetyLightPreset>) : RecyclerView.Adapter<SafetyLightAdapter.ViewHolder>() {

    inner class ViewHolder(var safetyLightPresetView: View, var context: Context) : RecyclerView.ViewHolder(safetyLightPresetView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val safetyLightPresetView = SafetyLightPresetView(parent.context)
        safetyLightPresetView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(safetyLightPresetView, parent.context)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        (holder.safetyLightPresetView as SafetyLightPresetView).setupView(SafetyLight.getValue()[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = songs.size

}