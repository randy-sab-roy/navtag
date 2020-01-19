package com.glados.navtag.ui.navtag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glados.navtag.R
import com.glados.navtag.core.NavTagList
import kotlinx.android.synthetic.main.fragment_navtag.*
import java.util.Observer

class NavTagFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_navtag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presetRecyclerView.layoutManager = LinearLayoutManager(context)
        viewAdapter = NavTagAdapter(NavTagList.getValue())
        presetRecyclerView.adapter = viewAdapter
    }

    override fun onStart() {
        instructions.text = "Add presets to the navtag by uploading them from their respective lists"
        NavTagList.addObserver(safetyLightObserver)
        NavTagList.reload()
        super.onStart()
    }

    override fun onStop() {
        NavTagList.deleteObserver(safetyLightObserver)
        super.onStop()
    }
}