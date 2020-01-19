package com.glados.navtag.ui.running

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glados.navtag.core.*

class RunningAdapter(private val presets: ArrayList<RunningPreset>) : RecyclerView.Adapter<RunningAdapter.ViewHolder>() {

    inner class ViewHolder(var runningPresetView: View, var context: Context) : RecyclerView.ViewHolder(runningPresetView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val runningPresetView = RunningPresetView(parent.context)
        runningPresetView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(runningPresetView, parent.context)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        (holder.runningPresetView as RunningPresetView).setupView(RunningList.getValue()[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = presets.size

}