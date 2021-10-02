package com.example.kampa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.kampa.models.Sitio
import com.google.android.gms.maps.model.LatLng
import com.parse.ParseGeoPoint
import com.example.kampa.Constantes
import com.example.kampa.models.TipoSitio
import com.parse.ParseQuery
import com.parse.PointerEncoder
import androidx.core.app.ActivityCompat.startActivityForResult

import androidx.annotation.NonNull
import com.google.android.gms.maps.*

import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import org.jetbrains.annotations.NotNull


class NuevoSitio : AppCompatActivity(), OnMapReadyCallback  {
    val TAG = "NuevoSitio"
    var listTipoSitio = mutableListOf<TipoSitio>()
    var map :MapView? = null
    var gMap : GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_sitio)

        initMap()

        desplegarTipoSitio()


        var sitio = Sitio()

        val agregarImagenButton: ImageButton = findViewById(R.id.agregarImagenButton)
        agregarImagenButton.setOnClickListener{
            val i = Intent(this, uploadImageActivity::class.java)

            startActivity(i)

        }

        val submitButtonSitio:Button = findViewById(R.id.submitButtonSitio)
        submitButtonSitio.setOnClickListener{

            val inputNombre:EditText = findViewById(R.id.inputNombre)
            sitio.nombre = inputNombre.text.toString()

            val inputDescripcion:EditText = findViewById(R.id.inputDescripcion)
            sitio.descripcion = inputDescripcion.text.toString()

            val inputHistoria:EditText = findViewById(R.id.inputHistoria)
            sitio.historia = inputHistoria.text.toString()

            val inputPagina:EditText = findViewById(R.id.inputPagina)
            sitio.paginaOficial = inputPagina.text.toString()

            val ubicacion = ParseGeoPoint(-30.0, 40.0)
            sitio.ubicacion = ubicacion

            val tipoSitio:RadioGroup = findViewById(R.id.radioGroupTipo)
            val idCheckedButton = tipoSitio.checkedRadioButtonId
            sitio.idTipoSitio = listTipoSitio[idCheckedButton]

            sitio.saveInBackground { e ->
                if (e == null) {
                    Log.d(TAG, "saved")
                } else {
                    Log.d(TAG, e.toString())
                }
            }
        }
    }

    fun desplegarTipoSitio(){
        var radioGroup:RadioGroup = findViewById(R.id.radioGroupTipo)
        val query: ParseQuery<TipoSitio> = ParseQuery.getQuery(TipoSitio::class.java)
        query.findInBackground { itemList, e ->
            if (e == null) {
                var id = 0
                for (el in itemList ) {
                    var radioButton = RadioButton(this)
                    radioButton.text = el.descripcion
                    radioButton.id = id
                    radioGroup.addView(radioButton, -1)
                    listTipoSitio.add(id, el)
                    id = id + 1
                }
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }
    }

    private fun initMap() {
        Log.d(TAG, "initMap: initializing map")

        map = findViewById(R.id.mapCreate) as MapView
        map?.onCreate(null)
        map?.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: entered onMapReady")
        gMap = googleMap
//        val latLng = LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
//        mMap.moveCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                latLng,
//                18f
//            )
//        )
//        mMap.setOnMapClickListener(OnMapClickListener {
//            Log.d(TAG, "onMapClick: clicked on map!")
//            val intent = Intent(this@CreateActivity, NewLocationActivity::class.java)
//            intent.putExtra("currentLocation", currentLocation)
//            startActivityForResult(intent, NEW_LOCATION_ACTIVITY_REQUEST_CODE)
//        })
    }
}