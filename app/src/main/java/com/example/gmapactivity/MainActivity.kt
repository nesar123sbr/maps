package com.example.gmapactivity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var gmap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gmap = googleMap

        // Add multiple markers
        val locations = listOf(
            LatLng(0.7893, 113.9213) to "Indonesia",
            LatLng(-6.2088, 106.8456) to "Jakarta",
            LatLng(-8.4095, 115.1889) to "Bali"
        )

        for ((coordinates, title) in locations) {
            gmap.addMarker(
                MarkerOptions()
                    .position(coordinates)
                    .title(title)
                    .snippet("Click to explore $title")
            )
        }

        // Move camera to the first marker
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0].first, 5f))

        // Enable UI controls
        gmap.uiSettings.isZoomControlsEnabled = true
        gmap.uiSettings.isMapToolbarEnabled = true

        // Enable location button and add user location marker
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        gmap.isMyLocationEnabled = true

        // Fetch and pin the user's current location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                gmap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("Your Location")
                )
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
            }
        }

        // Set map type options
        gmap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Optional: Add a custom map style (JSON file should be in raw resources)
        // val success = gmap.setMapStyle(
        //     MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
        // )
    }
}
