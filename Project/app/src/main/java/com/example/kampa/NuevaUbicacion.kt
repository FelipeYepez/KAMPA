package com.example.kampa


import android.content.Intent
import com.google.android.gms.common.api.Status
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
import java.util.*

/**
 * @author RECON
 * Nueva Ubicacion
 *
 * Esta actividad se encarga de abrir una nueva ventana para que el usuario pueda escoger la ubicación
 * del sitio que esta editando o creando
 *
 * @return  returns an extra which contains  the selected latitude an longitude
 */
class NuevaUbicacion : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var currentLocation: Location? = null
    var btnSave: Button? = null
    val TAG:String = "NewLocationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_ubicacion)
        Log.d(TAG, "onCreate: entered onCreate")


        //Declarar varables de los componentes de la vista
        btnSave = findViewById(R.id.btnSave)
        mapView = findViewById(R.id.mapNewLocation)
        currentLocation = intent.getParcelableExtra("currentLocation")
        Log.d(TAG, "currentLocation: $currentLocation")

        //Inicializar el fragmento del Mapa
        if (mapView != null) {
            mapView!!.onCreate(null)
            mapView!!.onResume()
            mapView!!.getMapAsync(this)
        }
        //Regresar la latitud y longitud en un extra al hacer click en guardar
        btnSave!!.setOnClickListener {
                // Preparar intent con la información
                val i = Intent()
                // Guardar la informacion de la latitud y longitud
                val latLngLocation = map!!.cameraPosition.target
                i.putExtra("latitude", latLngLocation.latitude)
                i.putExtra("longitude", latLngLocation.longitude)

                setResult(RESULT_OK, i)
                finish()//regresa a la actividad anterior editar o crear sitio
        }

        /**
         * Configuración de Places para la barra de busqueda
         */

        //Inicializar la barra de busqueda
        val autocompleteFragment: AutocompleteSupportFragment? =
            supportFragmentManager.findFragmentById(R.id.autocompleteNewLocation) as AutocompleteSupportFragment?
        if (!Places.isInitialized()) {
            Places.initialize(this, GOOGLE_MAPS_API_KEY)
        }
        val placesClient: PlacesClient = Places.createClient(this)

        //Declarar los atributos que tendra cada resultado de la busqueda
        autocompleteFragment?.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        // Crear un listener para cuando el usuario seleccione un lugar de los resultados de busqueda
        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            /**
             * @author RECON
             * Esta funcion centra la camara en el resultado de busqueda selecionado por el usuario
             *
             * @param place resultado de busqueda selecionado por el usuario
             */
           override fun onPlaceSelected(place: Place) {
                Log.i(
                    TAG,
                    "Place: " + place.getName().toString() + ", " + place.getId()
                        .toString() + ", " + place.getLatLng()
                )
                map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18f))
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }


    /**
     * @author RECON
     * Esta funcion inicializa el mapa
     * Al crear un sitio pone como vista inicial la ubicacion actual de el usuario
     * Al modificar un sitio pone como vista inicial la ultima ubicacion guardada de el siotio
     * 
     * @param googleMap  Mapa en el cual se observa la ubicacion selecionada indicada por un pin en el centro
     */
    override fun onMapReady(@NotNull googleMap: GoogleMap) {
        Log.d(TAG, "Entered map")
        map = googleMap
        val currentLocationLatLng =
            LatLng(currentLocation!!.getLatitude(), currentLocation!!.getLongitude())
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 18f))
    }



}