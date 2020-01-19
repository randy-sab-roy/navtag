package com.glados.navtag.ui.commute

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glados.navtag.core.*

class CommuteChoiceAdapter(private val presets: ArrayList<Route>) : RecyclerView.Adapter<CommuteChoiceAdapter.ViewHolder>() {

    inner class ViewHolder(var commuteChoiceView: View, var context: Context) : RecyclerView.ViewHolder(commuteChoiceView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val commutePresetView = CommutePresetView(parent.context)
        commutePresetView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(commutePresetView, parent.context)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        (holder.commuteChoiceView as CommuteChoiceView).setupView(CommuteList.getValue()[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = presets.size

}