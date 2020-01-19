package com.glados.navtag.ui.commute

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glados.navtag.R
import com.glados.navtag.core.CommuteList
import com.glados.navtag.core.DestinationList
import kotlinx.android.synthetic.main.fragment_widget.*
import java.util.Observer

class CommuteFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    private val commuteObserver = Observer { _, _ ->
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
        return inflater.inflate(R.layout.fragment_widget, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presetRecyclerView.layoutManager = LinearLayoutManager(context)
        viewAdapter = CommuteAdapter(CommuteList.getValue())
        presetRecyclerView.adapter = viewAdapter
    }

    override fun onStart() {
        fab.setOnClickListener {
            startActivity(Intent(context, CommuteActivity::class.java))
        }
        CommuteList.addObserver(commuteObserver)
        CommuteList.reload()
        super.onStart()
    }

    override fun onStop() {
        CommuteList.deleteObserver(commuteObserver)
        super.onStop()
    }
}