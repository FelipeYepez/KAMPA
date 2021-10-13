package com.example.kampa


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.GoogleMap
import android.location.Location
import android.util.Log
import android.widget.Button
import com.example.kampa.BuildConfig.GOOGLE_MAPS_API_KEY
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.jetbrains.annotations.NotNull


class NewLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    // Components
    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var currentLocation: Location? = null
    var btnSave: Button? = null
    val TAG:String = "NewLocationActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_location)
        Log.d(TAG, "onCreate: entered onCreate")


        // Finding view components
        btnSave = findViewById(R.id.btnSave)
        mapView = findViewById(R.id.mapNewLocation)
        currentLocation = intent.getParcelableExtra("currentLocation")
        Log.d(TAG, "currentLocation: $currentLocation")
        if (mapView != null) {
            mapView!!.onCreate(null)
            mapView!!.onResume()
            mapView!!.getMapAsync(this)
        }

        btnSave!!.setOnClickListener {
                // Prepare data intent
                val i = Intent()
                // Pass relevant data back as a result
                val latLngLocation = map!!.cameraPosition.target
                i.putExtra("latitude", latLngLocation.latitude)
                i.putExtra("longitude", latLngLocation.longitude)
                // Activity finished ok, return the data
                setResult(RESULT_OK, i) // set result code and bundle data for response
                finish() // closes the activity, pass data to parent
        }
    }



        /*********************************
         * Google Places API configuration
         */

        // Initialize the AutocompleteSupportFragment.
//        val autocompleteFragment: AutocompleteSupportFragment? =
//            supportFragmentManager.findFragmentById(R.id.autocompleteNewLocation) as AutocompleteSupportFragment?
//
//        if (!Places.isInitialized()) {
//            Places.initialize(this, GOOGLE_MAPS_API_KEY))
//        }
//        val placesClient: PlacesClient = Places.createClient(this)
//
//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(
//            Arrays.asList(
//                Place.Field.ID,
//                Place.Field.NAME,
//                Place.Field.LAT_LNG
//            )
//        )
//
//         Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener() {
//            fun onPlaceSelected(place: Place) {
//                Log.i(
//                    TAG,
//                    "Place: " + place.getName().toString() + ", " + place.getId()
//                        .toString() + ", " + place.getLatLng()
//                )
//                map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18f))
//            }
//
//            fun onError(status: Status) {
//                Log.i(TAG, "An error occurred: $status")
//            }
//        })
//
//    }

    override fun onMapReady(@NotNull googleMap: GoogleMap) {
        Log.d(TAG, "Entered map")
        map = googleMap
        val currentLocationLatLng =
            LatLng(currentLocation!!.getLatitude(), currentLocation!!.getLongitude())
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 18f))
    }



}