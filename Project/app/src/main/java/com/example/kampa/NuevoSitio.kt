package com.example.kampa

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
import androidx.core.app.ActivityCompat.startActivityForResult








class NuevoSitio : AppCompatActivity(), OnMapReadyCallback  {
    val TAG = "NuevoSitio"
    var listTipoSitio = mutableListOf<TipoSitio>()
    var map :MapView? = null
    var gMap : GoogleMap? = null
    var imagenSitio:ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_sitio)

        initMap()

        desplegarTipoSitio()

        var sitio = Sitio()
        imagenSitio = findViewById(R.id.imagenSitio)

        var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                var bitmap = loadFromUri(intent?.data)
                imagenSitio!!.setImageBitmap(bitmap)
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

            // pass the constant to compare it
            // with the returned requestCode
            startForResult.launch(Intent.createChooser(i, "Select Picture"))

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

    private fun loadImages(foto: ParseFile?, imgView: ImageView){
        if (foto != null) {
            foto.getDataInBackground(GetDataCallback { data, e ->
                if (e == null) {
                    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                    imgView.setImageBitmap(bmp)
                }
                else{
                    Log.d(TAG, e.toString())

                }
            })
        }
        else{
            Log.d(TAG, "Foto = NULL")
        }
    }

    fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source = ImageDecoder.createSource(
                    this.contentResolver,
                    photoUri!!
                )
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