package com.glados.navtag.ui.destination

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.glados.navtag.R
import com.glados.navtag.core.*
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_destination.*
import kotlinx.android.synthetic.main.activity_destination.name
import kotlinx.android.synthetic.main.activity_destination.saveButton


class DestinationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination)
        title = "Create New Destination Preset"
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private val mLocationRequest = LocationRequest.create()
        .setInterval(2)
        .setFastestInterval(1)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            for (location in result!!.locations) {
                if (location != null) {
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng))
                    break
                }
            }
        }
    }

    private fun getLocationFromAddress(strAddress: String): LatLng? {

        val coder = Geocoder(this)
        var p1: LatLng

        try {
            val address = coder.getFromLocationName(strAddress, 5) ?: return null
            Log.i("Address Found", address.toString())
            val location = address[0]

            p1 = LatLng(
                location.latitude,
                location.longitude
            )

            return p1
        } catch (ex: Exception) {
            Log.e("GEOPOINT EXCEPTION(", ex.toString())
        }
        return null
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                } else {
                    fusedLocationClient.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        null
                    )
                }
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }

    }

    override fun onStart() {
        saveButton.setOnClickListener {
            when {
                name.text.toString().isEmpty() -> {
                    name.error = "Preset name can't be empty"
                }
                addressField.text.toString().isEmpty() -> {
                    addressField.error = "Address Field can't be empty"
                }
                else -> {
                    DestinationList.addElement(
                        DestinationPreset(
                            name.text.toString(),
                            addressField.text.toString()
                        )
                    )
                    super.onBackPressed()
                }
            }
        }
        addressField.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE) {
                if (event == null || !event.isShiftPressed) {
                    // the user is done typing.
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(getLocationFromAddress(addressField.text.toString())!!).title(addressField.text.toString()))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocationFromAddress(addressField.text.toString()), 16f))
                    true // consume.
                }
            }
            false // pass on to other listeners.
        }

        super.onStart()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
