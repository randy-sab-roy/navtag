package com.glados.navtag.ui.destination

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glados.navtag.core.DestinationList
import com.glados.navtag.core.DestinationPreset
import com.glados.navtag.core.SafetyLight
import com.glados.navtag.core.SafetyLightPreset

class DestinationAdapter(private val presets: ArrayList<DestinationPreset>) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {

    inner class ViewHolder(var safetyLightPresetView: View, var context: Context) : RecyclerView.ViewHolder(safetyLightPresetView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val safetyLightPresetView = DestinationPresetView(parent.context)
        safetyLightPresetView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(safetyLightPresetView, parent.context)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        (holder.safetyLightPresetView as DestinationPresetView).setupView(DestinationList.getValue()[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = presets.size

}