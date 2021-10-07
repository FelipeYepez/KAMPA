package com.example.kampa

import android.Manifest
import android.R.attr
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
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.net.URI
import android.database.Cursor

import android.R.attr.data
import android.R.attr.path
import java.io.ByteArrayOutputStream

import java.io.InputStream










class NuevoSitio : AppCompatActivity(), OnMapReadyCallback  {
    val TAG = "NuevoSitio"
    var listTipoSitio = mutableListOf<TipoSitio>()
    var map :MapView? = null
    var gMap : GoogleMap? = null
    var imagenSitio:ImageView? = null
    var selectedBitmapImage: Bitmap? = null
    var selectedUriImage: Uri? = null
    var permission: Boolean? = null
    var currentLocation: Location? = null
    val NEW_LOCATION_ACTIVITY_REQUEST_CODE = 942
    lateinit var startFR : ActivityResultLauncher<Intent>


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

                if (intent?.data != null && "content".equals(intent!!.data?.getScheme())) {
                    selectedBitmapImage = bitmapFromUri(intent?.data)
                    imagenSitio!!.setImageBitmap(selectedBitmapImage)
                }
                else{
                    selectedUriImage = intent?.data
                    imagenSitio!!.setImageURI(selectedUriImage)
                }
            }
        }

        startFR = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val latLngLocation = LatLng(intent!!.getDoubleExtra("latitude",0.0),intent!!.getDoubleExtra("longitude",0.0))
                Log.d(TAG,latLngLocation.toString())
                currentLocation!!.latitude =latLngLocation.latitude
                currentLocation!!.longitude =latLngLocation.longitude

                gMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 18f))



            }
        }

        val capturarImagenButton: Button = findViewById(R.id.capturarImagenButton)
        capturarImagenButton.setOnClickListener{

            startForResult.launch(Intent(this, UploadImageActivity::class.java))
        }

        val galeriaImagenButton: Button = findViewById(R.id.galeriaImagenButton)
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

            if(selectedBitmapImage != null){
                val stream = ByteArrayOutputStream()
                selectedBitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                sitio.foto = ParseFile("Sitio" + (0..1000000000).random().toString() + "-" +".png" , byteArray)
            }

            else if(selectedUriImage != null){
                sitio.foto = ParseFile(File(selectedUriImage?.path))
            }

            val inputDescripcion:EditText = findViewById(R.id.inputDescripcion)
            sitio.descripcion = inputDescripcion.text.toString()

            val inputHistoria:EditText = findViewById(R.id.inputHistoria)
            sitio.historia = inputHistoria.text.toString()

            val inputPagina:EditText = findViewById(R.id.inputPagina)
            sitio.paginaOficial = inputPagina.text.toString()

            val ubicacion = ParseGeoPoint(currentLocation!!.latitude,currentLocation!!.longitude)
            sitio.ubicacion = ubicacion

            val tipoSitio:RadioGroup = findViewById(R.id.radioGroupTipo)
            val idCheckedButton = tipoSitio.checkedRadioButtonId
            if(idCheckedButton != -1){
                sitio.idTipoSitio = listTipoSitio[idCheckedButton]
            }

            if(sitio.nombre!!.length > 0 && sitio.descripcion!!.length > 0 && idCheckedButton != -1){
                Log.d(TAG, "dentro de if")
                sitio.saveInBackground { e ->
                    if (e == null) {
                        Log.d(TAG, "saved")
                    } else {
                        Log.d(TAG, e.toString())
                    }
                }
                finish()
            }
            else{
                Toast.makeText(this, "Llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
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

        gMap?.setOnMapClickListener(GoogleMap.OnMapClickListener {
            Log.d(TAG, "onMapClick: clicked on map!")
            val i = Intent(this, NewLocationActivity::class.java)
            i.putExtra("currentLocation", currentLocation)

            startFR.launch(i)


        })
    }

    fun bitmapFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source: ImageDecoder.Source = ImageDecoder.createSource(this.contentResolver, photoUri!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }


}