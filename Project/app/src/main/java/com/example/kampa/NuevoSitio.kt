package com.example.kampa

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kampa.models.Sitio
import com.parse.ParseGeoPoint
import com.example.kampa.models.TipoSitio
import com.parse.ParseQuery

import com.google.android.gms.maps.*
import com.parse.GetDataCallback
import com.parse.ParseFile
import java.io.IOException

import android.provider.MediaStore

import android.graphics.ImageDecoder

import android.os.Build

import android.graphics.Bitmap
import android.location.Location
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.net.URI


class NuevoSitio : AppCompatActivity(), OnMapReadyCallback  {
    val TAG = "NuevoSitio"
    var listTipoSitio = mutableListOf<TipoSitio>()
    var map :MapView? = null
    var gMap : GoogleMap? = null
    var imagenSitio:ImageView? = null
    var selectedImage: Uri? = null
    var permission: Boolean? = null
    var currentLocation: Location? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_sitio)


        initMap()

        desplegarTipoSitio()

        permission = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("permission") as Boolean
        } else {
            savedInstanceState.getSerializable("permission") as Boolean
        }

        currentLocation = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("currentLocation") as Location
        } else {
            savedInstanceState.getSerializable("currentLocation") as Location
        }

        var sitio = Sitio()
        imagenSitio = findViewById(R.id.imagenSitio)

        var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                selectedImage = intent?.data
                imagenSitio!!.setImageURI(selectedImage)
            }
        }

        val capturarImagenButton: ImageButton = findViewById(R.id.capturarImagenButton)
        capturarImagenButton.setOnClickListener{

            startForResult.launch(Intent(this, UploadImageActivity::class.java))

        }

        val galeriaImagenButton: ImageButton = findViewById(R.id.galeriaImagenButton)
        galeriaImagenButton.setOnClickListener{
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT

            startForResult.launch(Intent.createChooser(i, "Select Picture"))

        }

        val submitButtonSitio:Button = findViewById(R.id.submitButtonSitio)
        submitButtonSitio.setOnClickListener{

            val inputNombre:EditText = findViewById(R.id.inputNombre)
            sitio.nombre = inputNombre.text.toString()

            if(selectedImage != null){
                sitio.foto = ParseFile(File(selectedImage?.path))
            }

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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: entered onMapReady")
        gMap = googleMap
        gMap?.setMyLocationEnabled(permission!!)

       val latLng = LatLng(currentLocation!!.getLatitude(), currentLocation!!.getLongitude())
        gMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                18f
            )
        )
//        mMap.setOnMapClickListener(OnMapClickListener {
//            Log.d(TAG, "onMapClick: clicked on map!")
//            val intent = Intent(this@CreateActivity, NewLocationActivity::class.java)
//            intent.putExtra("currentLocation", currentLocation)
//            startActivityForResult(intent, NEW_LOCATION_ACTIVITY_REQUEST_CODE)
//        })
    }




}