package com.example.kampa.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import com.google.android.gms.tasks.Task
import com.example.kampa.*
import com.example.kampa.R
import android.location.Location
import android.widget.RadioButton
import android.widget.RadioGroup

import org.jetbrains.annotations.NotNull

import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.example.kampa.models.*

import com.google.android.gms.tasks.OnCompleteListener

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.parse.*
import com.parse.ParseObject.createWithoutData
import java.util.jar.Manifest
import com.parse.ParseObject

import com.parse.ParseQuery




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "MapaFragment"
private const val ZOOM : Float = 15F

/**
 * A simple [Fragment] subclass.
 * Use the [MapaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapaFragment : Fragment(), OnMapReadyCallback ,GoogleMap.OnMarkerClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var map: SupportMapFragment? = null
    private var gMap: GoogleMap? = null
    private var roleObject: Rol? = null
    var currentRole:Rol = ParseUser.getCurrentUser().get("idRol") as Rol
    private lateinit var mainActivity: MainActivity
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var  currentLocation: Location

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()
        roleQuery(currentRole.objectId.toString())


        var fab: FloatingActionButton = view.findViewById(R.id.NuevoSitio)


        if(roleObject?.descripcion == "administrador"){
            fab.visibility = View.VISIBLE
            fab.setOnClickListener {

                val i = Intent(activity, NuevoSitio::class.java)

                i.putExtra("permission", mainActivity.isPermissionGranted)
                i.putExtra("currentLocation",currentLocation)

                startActivity(i)
            }
        }

        else{
            fab.visibility = View.INVISIBLE
        }



    }

    private fun initMap() {
        map = childFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as? SupportMapFragment

        map?.getMapAsync(this)

        query()
    }

    private fun query() {

        val query: ParseQuery<Sitio> = ParseQuery.getQuery(Sitio::class.java)

        query.findInBackground { itemList, e ->
            if (e == null) {
                // Access the array of results here
                for (el in itemList) {
                    add_Marker(el)
                }

            } else {
                Log.d("item", "Error: " + e.message)

                if(e.code == 100){
                    Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun roleQuery(id:String){
        val query = ParseQuery<Rol>("Rol")
        try {
            roleObject = query[id]
        } catch (e: ParseException) {
            Log.d(TAG, e.toString())
        }
    }

    private fun add_Marker(sitio: Sitio) {
        var color = 0F
        var usuarioSitio = UsuarioSitio()
        val query: ParseQuery<UsuarioSitio> = ParseQuery.getQuery(UsuarioSitio::class.java)
        query.whereEqualTo("idUsuario", ParseUser.getCurrentUser())
        query.whereEqualTo("idSitio", sitio)
        query.findInBackground { itemList, e ->
            if (e == null && itemList.size > 0) {
                usuarioSitio = itemList.get(0)

                if(usuarioSitio.isVisitado == true){
                    color = 260F
                }
                else if(usuarioSitio.isWishlist == true){
                    color = 47F
                }

            } else if(e != null) {
                Log.d(TAG, "Error: " + e.message)
            } else{
                Log.d(TAG, "Vacio")
            }

            val marker = gMap?.addMarker(
                MarkerOptions()
                    .title(sitio.nombre)
                    .position(LatLng(sitio.ubicacion!!.latitude, sitio.ubicacion!!.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
            )
            marker?.tag = sitio
        }
    }

    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        Log.d(TAG, "entered onclick ")

        val sitio: Sitio = marker.tag as Sitio

        val i = Intent(activity, SitioActivity::class.java)
        i.putExtra("permission", mainActivity.isPermissionGranted)
        i.putExtra("currentLocation",currentLocation)
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
        gMap = p0
        gMap?.setMyLocationEnabled(mainActivity.isPermissionGranted!!)
        gMap?.setOnMarkerClickListener(this)
        getDeviceLocation()

    }

    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    fun getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device's current location")
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            if (mainActivity.isPermissionGranted!!) {
                val location: Task<Location> = mFusedLocationProviderClient.getLastLocation()
                location.addOnCompleteListener(OnCompleteListener<Location> { task ->
                    if (task.isSuccessful && task.result != null) {
                        Log.d(TAG, "onComplete: found location!")
                        currentLocation = task.result as Location


                        gMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), ZOOM)
                        )
                    } else {
                        Log.d(TAG, "onComplete: current location is null")
                        Toast.makeText(
                            context,
                            "Unable to get current location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.message)
        }
    }

}