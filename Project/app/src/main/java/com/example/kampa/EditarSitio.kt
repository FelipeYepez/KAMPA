package com.example.kampa

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kampa.models.Sitio
import com.example.kampa.models.TipoSitio
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.parse.GetDataCallback
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseQuery
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class EditarSitio : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "EditarSitio"

    private lateinit var etNombre: EditText
    private lateinit var btnGaleria: Button
    private lateinit var btnCamara: Button
    private lateinit var ivFotoSitio: ImageView
    var mvLocalizacion: MapView? = null
    private lateinit var ivPinPoint: ImageView
    private lateinit var etDescripcion: EditText
    private lateinit var etHistoria: EditText
    private lateinit var rgCategoria: RadioGroup
    private lateinit var etPaginaOficial: EditText
    private lateinit var btnConfirmarCambios: Button
    private lateinit var locationMap: GoogleMap

    var listTipoSitio = mutableListOf<TipoSitio>()
    private var selectedBitmapImage: Bitmap? = null
    private var selectedUriImage: Uri? = null
    private var permission: Boolean? = null
    private var currentLocation: Location? = null
    private var sitioUbicacion: Location? = null
    private lateinit var setPhotoForResult: ActivityResultLauncher<Intent>
    private lateinit var setLocationForResult: ActivityResultLauncher<Intent>

    private lateinit var sitio: Sitio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_sitio)

        getExtras(savedInstanceState)
        initMap()
        initializeVariables()
    }

    private fun getExtras(savedInstanceState: Bundle?) {
        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get(Constantes.SITIO) as Sitio
        } else {
            savedInstanceState.getSerializable(Constantes.SITIO) as Sitio
        }

        permission = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("permission") as? Boolean
        } else {
            savedInstanceState.getSerializable("permission") as? Boolean
        }

        currentLocation = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("currentLocation") as? Location
        } else {
            savedInstanceState.getSerializable("currentLocation") as? Location
        }

        sitioUbicacion = currentLocation
        if (sitio.ubicacion != null) {
            sitioUbicacion?.longitude = sitio.ubicacion!!.longitude
            sitioUbicacion?.latitude = sitio.ubicacion!!.latitude
        }
    }

    private fun initializeVariables() {
        setLocationForResult = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                val latLngLocation = LatLng(
                    resultData!!.getDoubleExtra("latitude",0.0),
                    resultData!!.getDoubleExtra("longitude",0.0)
                )

                sitioUbicacion!!.latitude = latLngLocation.latitude
                sitioUbicacion!!.longitude = latLngLocation.longitude

                locationMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 18f))
            }
        }

        etNombre = findViewById(R.id.etNombre)
        etNombre.setText(sitio.nombre)

        ivFotoSitio = findViewById(R.id.ivFotoSitio)
        loadFoto(sitio.foto)
        setPhotoForResult = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                if (resultData?.data != null && "content".equals(resultData.data?.getScheme())) {
                    selectedBitmapImage = bitmapFromUri(resultData.data)!!
                    ivFotoSitio.setImageBitmap(selectedBitmapImage)
                } else {
                    selectedUriImage = resultData?.data!!
                    ivFotoSitio.setImageURI(selectedUriImage)
                }
            }
        }

        btnGaleria = findViewById(R.id.btnGaleria)
        btnGaleria.setOnClickListener {
            val galeria = Intent()
            galeria.type = "image/*"
            galeria.action = Intent.ACTION_GET_CONTENT

            setPhotoForResult.launch(Intent.createChooser(galeria, "Select Picture"))
        }

        btnCamara = findViewById(R.id.btnCamara)
        btnCamara.setOnClickListener {
            setPhotoForResult.launch(Intent(this, UploadImageActivity::class.java))
        }

        rgCategoria = findViewById(R.id.rgCategoria)
        desplegarTipoSitio()

        etDescripcion = findViewById(R.id.etDescripcion)
        etDescripcion.setText(sitio.descripcion)

        etHistoria = findViewById(R.id.etHistoria)
        etHistoria.setText(sitio.historia)

        etPaginaOficial = findViewById(R.id.etPaginaOficial)
        etPaginaOficial.setText(sitio.paginaOficial)

        btnConfirmarCambios = findViewById(R.id.btnConfirmarCambios)
        btnConfirmarCambios.setOnClickListener {
            confirmarCambios()
        }
    }

    private fun initMap() {
        mvLocalizacion = findViewById(R.id.mvLocalizacion) as MapView
        mvLocalizacion?.onCreate(null)
        mvLocalizacion?.getMapAsync(this)
    }

    private fun desplegarTipoSitio() {
        val query: ParseQuery<TipoSitio> = ParseQuery.getQuery(TipoSitio::class.java)
        query.findInBackground { itemList, e ->
            if (e == null) {
                var id = 0
                for (item in itemList) {
                    var radioButton = RadioButton(this)
                    radioButton.text = item.descripcion
                    radioButton.id = id
                    if (item.objectId == sitio.idTipoSitio?.objectId.toString()) {
                        radioButton.setChecked(true)
                    }

                    rgCategoria.addView(radioButton, -1)
                    listTipoSitio.add(id, item)
                    id = id + 1
                }
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }
    }

    private fun loadFoto(fotoSitio: ParseFile?){
        if (fotoSitio != null) {
            fotoSitio.getDataInBackground(GetDataCallback { data, e ->
                if (e == null) {
                    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                    ivFotoSitio.setImageBitmap(bmp)
                } else{
                    Log.d(TAG, e.toString())
                }
            })
        } else {
            Log.d(TAG, "No hay foto registrada de este lugar")
        }
    }

    private fun bitmapFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            image = if (Build.VERSION.SDK_INT > 27) {
                val source: ImageDecoder.Source = ImageDecoder.createSource(
                    this.contentResolver,
                    photoUri!!
                )
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return image
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(_locationMap: GoogleMap) {
        locationMap = _locationMap
        locationMap.setMyLocationEnabled(permission!!)

        val latLng = LatLng(sitioUbicacion!!.getLatitude(), sitioUbicacion!!.getLongitude())
        locationMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                18f
            )
        )

        locationMap.setOnMapClickListener(GoogleMap.OnMapClickListener {
            val newLocation = Intent(this, NuevaUbicacion::class.java)
            newLocation.putExtra(Constantes.CURRENT_LOCATION, sitioUbicacion)
            setLocationForResult.launch(newLocation)
        })
    }

    private fun confirmarCambios() {
        sitio.nombre = etNombre.text.toString()
        sitio.descripcion = etDescripcion.text.toString()
        sitio.historia = etHistoria.text.toString()
        sitio.paginaOficial = etPaginaOficial.text.toString()

        if (selectedBitmapImage != null){
            val stream = ByteArrayOutputStream()
            selectedBitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)

            val byteArray = stream.toByteArray()
            val nombreFoto = "Sitio" + (0..1000000000).random().toString() + "-" +".png"
            sitio.foto = ParseFile(nombreFoto, byteArray)
        } else if (selectedUriImage != null){
            sitio.foto = ParseFile(File(selectedUriImage?.path))
        }

        val ubicacion = ParseGeoPoint(sitioUbicacion!!.latitude, sitioUbicacion!!.longitude)
        sitio.ubicacion = ubicacion

        val idCheckedButton = rgCategoria.checkedRadioButtonId
        if (idCheckedButton != -1) {
            sitio.idTipoSitio = listTipoSitio[idCheckedButton]
        }

        if (sitio.nombre!!.length > 0 || sitio.descripcion!!.length > 0 || idCheckedButton != -1) {
            sitio.saveInBackground { e ->
                if (e == null) {
                    Toast.makeText(
                        this,
                        "Sitio modificado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Log.d(TAG, e.toString())
                    finish()
                }
            }
        } else{
            Toast.makeText(
                this,
                "Llena todos los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}