package com.glados.navtag.ui.commute

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import com.glados.navtag.R
import com.glados.navtag.core.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_commute.*
import org.json.JSONObject
import java.io.InputStream

class CommuteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Create New Commute Preset"
        setContentView(R.layout.activity_commute)
    }

    private var busLineIntersections = ArrayList<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onStart() {
        super.onStart()

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        val string = readJSONFromAsset("intersections.json")
        val intersections = Gson().fromJson<IntersectionData>(string, IntersectionData::class.java)

        val string2 = readJSONFromAsset("routes.json")
        val routes = Gson().fromJson<Array<Route>>(string2, Array<Route>::class.java)
        convertRoutes(routes)

        saveButton.setOnClickListener {
            if (name.text.toString().isEmpty()) {
                name.error = "Preset name can't be empty"
            } else if (!routes.any { it.id == busLine.text.toString() }){
                busLine.error = "Bus line number does not exist"
            }
            else {
                CommuteList.addElement(
                    CommutePreset(
                        name.text.toString(),
                        name.text.toString()
                    )
                )
                super.onBackPressed()
            }
        }
        busLine.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                if (event == null || !event.isShiftPressed) {
                    // the user is done typing.
                    if (routes.any { it.id == busLine.text.toString() }){
                        busLineIntersections = filterChoices(
                            intersections,
                            routes[routes.indexOf(routes.find { it.id == busLine.text.toString() })].intersections
                        )
                        arrayAdapter.clear()
                        arrayAdapter.addAll(busLineIntersections)
                        arrayAdapter.notifyDataSetChanged()
                    }
                    else {
                        busLine.error = "Bus line number does not exist"
                        arrayAdapter.clear()
                        arrayAdapter.notifyDataSetChanged()
                    }
                    true // consume.
                }
            }
            false // pass on to other listeners.
        }

    }

    private fun convertRoutes(routes: Array<Route>) {
        for (route in routes) {
            var quotient = route.id.toInt() / 10
            val modulus = route.id.toInt() % 10
            if (modulus == 0) {
                if (quotient % 2 == 0)
                    route.id = "$quotient" + "N"
                else
                    route.id = "$quotient" + "E"
            } else {
                if (quotient % 2 == 0)
                    route.id = "$quotient" + "S"
                else
                    route.id = "$quotient" + "W"
            }
        }
    }

    private fun filterChoices(
        data: IntersectionData,
        intersections: IntArray
    ): ArrayList<String> {
        val list = ArrayList<String>()
        for (element in data.data) {
            if (intersections.contains(element.id)) {
                list.add(element.intersection)
            }
        }
        return list
    }

    private fun readJSONFromAsset(fileName: String): String? {
        val json: String
        try {
            val inputStream: InputStream = applicationContext.assets.open(fileName)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}
