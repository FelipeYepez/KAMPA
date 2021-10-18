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

import androidx.annotation.RequiresPermission
import com.example.kampa.models.*

import com.google.android.gms.tasks.OnCompleteListener

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.parse.*

import com.parse.ParseQuery

private const val TAG = "MapaFragment"
private const val ZOOM : Float = 15F

/**
 * Fragmento que contiene el mapa principal de la aplicación
 */
class MapaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private var map: SupportMapFragment? = null
    private var gMap: GoogleMap? = null
    private var roleObject: Rol? = null
    private var fab: FloatingActionButton? = null
    private var currentRole:Rol = ParseUser.getCurrentUser().get("idRol") as Rol
    private lateinit var mainActivity: MainActivity
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var  currentLocation: Location

    /**
     * Inicializa el fragmento
     * @param savedInstanceState para que la actividad pueda restaurarse a un estado anterior
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = requireActivity() as MainActivity
    }

    /**
     * Infla la vista con el layout correspondiente
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }

    /**
     * Cuando se crea la vista se inicializa el mapa y se añade un float action button si el usuario
     * en sesión es administrador
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab = view.findViewById(R.id.NuevoSitio)
        initMap()
        roleQuery(currentRole.objectId.toString())
        setBtnVisibility()
    }

    /**
     * Se le añade un ClickListener al botón para crear sitio
     * La visibilidad del botón es hidden si el usuario no es administrador
     */
    private fun setBtnVisibility(){
        if(roleObject?.descripcion == "administrador"){
            fab?.visibility = View.VISIBLE
            fab?.setOnClickListener {
                val i = Intent(activity, NuevoSitioActivity::class.java)
                i.putExtra("permission", mainActivity.isPermissionGranted)
                i.putExtra("currentLocation",currentLocation)
                startActivity(i)
            }
        }
        else{
            fab?.visibility = View.INVISIBLE
        }
    }

    /**
     * Iniciliza el mapa
     * Llama a la función que crea los marcadores
     */
    private fun initMap() {
        map = childFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as? SupportMapFragment
        map?.getMapAsync(this)
        query()
    }

    /**
     * Obtiene el rol del usuario autenticado y lo guarda en roleObject
     * @param id el objectId del usuario
     */
    private fun roleQuery(id:String){
        val query = ParseQuery<Rol>("Rol")
        try {
            roleObject = query[id]
        } catch (e: ParseException) {
            Log.d(TAG, e.toString())
        }
    }

    /**
     * Obtiene una lista de sitios existentes en la base de datos
     */
    private fun query() {
        val query: ParseQuery<Sitio> = ParseQuery.getQuery(Sitio::class.java)
        query.findInBackground { itemList, e ->
            if (e == null) {
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

    /**
     * Añade un marcador en el mapa
     * Dependiendo de si el lugar es visitado, está en la lista de favoritos o no, se le asigna un
     * color diferente.
     * @param sitio lugar que será añadido al mapa
     */
    private fun add_Marker(sitio: Sitio) {
        var color = 0F
        var usuarioSitio: UsuarioSitio
        val query: ParseQuery<UsuarioSitio> = ParseQuery.getQuery(UsuarioSitio::class.java)
        query.whereEqualTo("idUsuario", ParseUser.getCurrentUser())
        query.whereEqualTo("idSitio", sitio)
        query.findInBackground { itemList, e ->
            if (e == null && itemList.size > 0) {
                usuarioSitio = itemList.get(0)
                color = MapaFragmentUtils.setColor(usuarioSitio.isVisitado!!, usuarioSitio.isWishlist!!)

            } else if(e != null) {
                Log.d(TAG, "Error: " + e.message)
            } else{
                Log.d(TAG, "Vacío")
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


    /**
     * Se inicia un intent con la clase sitioActivity del sitio que se selecciona en el mapa
     * @param marker marcador que se seleccionó
     * @return se regresa false ya que se termina de usar el marcador
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        val sitio: Sitio = marker.tag as Sitio
        val i = Intent(activity, SitioActivity::class.java)
        i.putExtra("permission", mainActivity.isPermissionGranted)
        i.putExtra("currentLocation",currentLocation)
        i.putExtra("sitio", sitio)
        startActivity(i)
        return false
    }

    /**
     * Muestra la ubicación del usuario en pantalla y un botón para ir a su ubicación.
     * Coloca ClickListeners para los marcadores.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap?) {
        gMap = p0
        gMap?.setMyLocationEnabled(mainActivity.isPermissionGranted!!)
        gMap?.setOnMarkerClickListener(this)
        getDeviceLocation()
    }

    /**
     * Obtiene la ubicación actual del usuario y la guarda en current location
     */
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

object MapaFragmentUtils{
    /**
     * Asigna un color a cada marcador del mapa
     * @param isVisitado es true si el lugar ha sido visitado
     * @param isWishlist es true si el lugar está en una lista de favoritos
     * @return regresa el color en flotante
     */
    fun setColor(isVisitado:Boolean, isWishlist: Boolean):Float{
        if(isVisitado == true){
            return 260F
        } else if(isWishlist == true){
            return 47F
        }
        return 0F
    }
}