package com.glados.navtag.ui.safetylight

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glados.navtag.R
import com.glados.navtag.core.SafetyLight
import kotlinx.android.synthetic.main.fragment_safety_light.*
import java.util.Observer

class SafetyLightFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    private val safetyLightObserver = Observer { _, _ ->
        if (viewAdapter.itemCount == 0) {
            instructions.visibility = View.VISIBLE
        } else {
            instructions.visibility = View.GONE
        }
        viewAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_safety_light, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presetRecyclerView.layoutManager = LinearLayoutManager(context)
        viewAdapter = SafetyLightAdapter(SafetyLight.getValue())
        presetRecyclerView.adapter = viewAdapter
    }

    override fun onStart() {
        instructions.text = "To add a preset, press the button below"
        fab.setOnClickListener {
            startActivity(Intent(context, SafetyLightActivity::class.java))
        }
        SafetyLight.addObserver(safetyLightObserver)
        SafetyLight.reload()
        super.onStart()
    }

    override fun onStop() {
        SafetyLight.deleteObserver(safetyLightObserver)
        super.onStop()
    }
}