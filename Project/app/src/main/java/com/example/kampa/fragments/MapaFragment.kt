package com.example.kampa.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kampa.models.Sitio
import com.example.kampa.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parse.ParseQuery
import android.content.Intent
import com.example.kampa.MainActivity
import com.example.kampa.SitioActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "MapaFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [MapaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapaFragment : Fragment(), OnMapReadyCallback ,GoogleMap.OnMarkerClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var map : SupportMapFragment? = null
    private  var gMap : GoogleMap? = null
    private  lateinit var mainActivity : MainActivity
    private var fab : FloatingActionButton? = null
    private var mLocationClient : FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mainActivity = requireActivity() as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()


    }

    private fun initMap() {
        map =childFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as? SupportMapFragment

        map?.getMapAsync(this)
        query()
    }

    private fun query(){
        val query: ParseQuery<Sitio> = ParseQuery.getQuery(Sitio::class.java)

        query.findInBackground { itemList, e ->
            if (e == null) {
                // Access the array of results here
                for (el in itemList ) {
                    add_Marker(el)
                    Log.d(TAG,el.nombre!!)
                }
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }
    }



    private fun add_Marker(sitio: Sitio) {
            val marker = gMap!!.addMarker(
                MarkerOptions()
                    .title(sitio.nombre)
                    .position(LatLng(sitio.ubicacion!!.latitude, sitio.ubicacion!!.longitude))

            )
        marker.tag = sitio
    }
    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        Log.d(TAG,"entered onclick ")

        val sitio : Sitio = marker.tag as Sitio

        val i = Intent(activity, SitioActivity::class.java)

        i.putExtra("sitio", sitio)

        startActivity(i)

        return false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                     putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap?) {
        Toast.makeText(context, "onMapReady", Toast.LENGTH_SHORT).show()
        gMap = p0
        gMap?.setMyLocationEnabled(mainActivity.isPermissionGranted!!)
        gMap?.setOnMarkerClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        map?.onStart()
    }

    override fun onResume() {
        super.onResume()
        map?.onResume()
    }

    override fun onPause() {
        super.onPause()
        map?.onPause()
    }

    override fun onStop() {
        super.onStop()
        map?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        map?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map?.onLowMemory()
    }
}